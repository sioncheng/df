package com.github.sioncheng.fs.act

import java.io._
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.event.Logging
import com.github.sioncheng.cnf.AppConfiguration
import com.github.sioncheng.prtl._
import com.github.sioncheng.prtl.outer._
import com.google.protobuf.ByteString

import scala.collection.mutable
import scala.concurrent.duration._

case class FileOperationException(code: Int, path: String, message: String, sourceId: String)
case class FinishedFileOperation(code: Int, path: String, sourceId: String)

case class FileCommandMessage(fc: FileCommand, sourceId: String)


class FileActor(val appConf: AppConfiguration, val mainActor: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[FileActor])
    val createFileActors : mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()
    val creatingFilePathSet: mutable.HashSet[String] =
        new mutable.HashSet[String]()
    val openFileActors: mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()

    override def receive: Receive = {
        case fcm : FileCommandMessage =>
            logger.info(s"received file command ${fcm.fc.commandCode}")
            processFileCommand(fcm.fc, fcm.sourceId)
        case ffo: FinishedFileOperation =>
            ffo.code match {
                case CommandCode.CreateFile =>
                    removeCreateFileActor(ffo.path)
            }
        case x =>
            logger.info(s"received $x")
    }


    override val supervisorStrategy =
        OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
            case _: ArithmeticException      => Resume
            case _: NullPointerException     => Restart
            case e: IllegalCreateFileMessageException =>
                logger.error(s"e.getMessage ${e.getMessage}")
                mainActor ! FileOperationException(CommandCode.CreateFile, e.path, e.getMessage, e.sourceId)
                removeCreateFileActor(e.path)
                Stop
            case e: IllegalOpenFileMessageException =>
                logger.error(s"e.getMessage ${e.getMessage}")
                mainActor ! FileOperationException(CommandCode.OpenFile, e.path, e.getMessage, e.sourceId)
                Stop
            case x: Exception                =>
                logger.warning(s"escalate ${x}")
                Escalate
        }

    def processFileCommand(fc: FileCommand, sourceId: String): Unit = {
        val root = appConf.getString("fs-root").get
        fc.commandCode match {
            case CommandCode.CreateFile =>
                val createFileMessage = CreateFile.CreateFileMessage.parseFrom(fc.data)
                if (false == creatingFilePathSet.contains(createFileMessage.getPath)) {
                    val props = Props.create(classOf[CreateFileActor],
                        root,
                        createFileMessage.getPath,
                        self,
                        sourceId)
                    val createFileActor = context.actorOf(props)
                    createFileActors.put(sourceId, createFileActor)
                    creatingFilePathSet.add(createFileMessage.getPath)
                }
                createFileActors.get(sourceId).head ! createFileMessage
            case CommandCode.DeleteFile =>
                val deleteFileMessage = DeleteFile.DeleteFileMessage.parseFrom(fc.data)
                val file = new File(root, deleteFileMessage.getPath)
                sender() ! DeleteFileResult(root, deleteFileMessage.getPath, file.delete())
            case CommandCode.FindFile =>
                val findFileMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                val file = new File(root, findFileMessage.getPath)
                sender() ! FindFileResult(root, findFileMessage.getPath, file.exists())
            case CommandCode.OpenFile =>
                if (false == openFileActors.contains(sourceId)) {
                    val openFileMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                    val props = Props.create(classOf[OpenFileActor],
                        root,
                        openFileMessage.getPath,
                        self,
                        sourceId)

                    val openFileActor = context.actorOf(props)
                    openFileActors.put(sourceId, openFileActor)
                }
                openFileActors.get(sourceId).head ! fc
            case x =>
                logger.warning(s"what? ${x}")
        }
    }


    private def removeCreateFileActor(path: String): Unit = {
        val createFileActor = createFileActors.get(path).get
        context.stop(createFileActor)
        createFileActors.remove(path)
    }

}


class CreateFileActor(root: String, path: String, parent: ActorRef, sourceId: String) extends Actor {

    val logger = Logging(context.system, classOf[CreateFileActor])

    var expectPartition: Int = 0

    var f : FileChannel = null

    override def postStop(): Unit = {
        if (f != null) {
            f.close()
            f = null
        }
        super.postStop()
    }

    override def receive: Receive = {
        case createFileMessage: CreateFile.CreateFileMessage =>

            if (createFileMessage.getPartitionNo() != expectPartition) {
                val message = s"expect ${expectPartition} but ${createFileMessage.getPartitionNo}"
                throw new IllegalCreateFileMessageException(path, message, sourceId)
            }

            if (f == null) {
                val file = new File(root, path)
                if (file.exists()) {
                    val message = s"${root}${path} exists"
                    throw new IllegalCreateFileMessageException(path, message, sourceId)
                }
                f = (new FileOutputStream(file, true)).getChannel
            }
            val bf = ByteBuffer.wrap(createFileMessage.getData.toByteArray)
            var remain = bf.limit()
            var index = 0
            try {
                while (remain > 0) {
                    val n = f.write(bf)
                    index = index + n
                    remain = remain - n
                }
            } catch {
                case e: IOException =>
                    logger.error(s"create ${path} error ${e.getMessage}")
                    sender() ! CreateFileResult(root, path, false)
                    parent ! IllegalCreateFileMessageException(path, e.getMessage)
            }

            //
            expectPartition = expectPartition + 1

            if (expectPartition == createFileMessage.getPartitions) {
                logger.info(s"finished write file ${createFileMessage.getPath}")

                sender() ! CreateFileResult(root, path, true)
                parent ! FinishedFileOperation(CommandCode.CreateFile, path, sourceId)
            }
        case x =>
            logger.warning(s"what ? ${x}")
    }
}

class OpenFileActor(root: String, path: String, parent: ActorRef, sourceId: String) extends Actor {
    val blockSize = 1024
    val logger = Logging(context.system, classOf[OpenFileActor])

    override def receive: Receive = {
        case _ : FilePath.FilePathMessage =>
            val file = new File(root, path)
            if (file.exists() == false) {
                throw new IllegalOpenFileMessageException(path, "file does not exist", sourceId)
            }
            val partitions = (file.length() + blockSize - 1) / blockSize
            val buffer = new Array[Byte](blockSize)
            val fs = new FileInputStream(file)
            var partition = 0
            var total = 0
            while (partition < partitions) {
                var index = 0
                var shouldLoop = true
                while(shouldLoop) {
                    val n = fs.read(buffer, index, buffer.length)
                    total = total + n
                    index = index + n
                    if (n < buffer.length && total < file.length()) {
                        shouldLoop = true
                    } else {
                        shouldLoop = false
                    }
                }

                val openFileMessage = OpenFile.OpenFileMessage.newBuilder()
                    .setPath(path)
                    .setContentLength(file.length())
                    .setPartitions(partitions.toInt)
                    .setPartitionNo(partition)
                    .setPartitionLength(index)
                    .setData(ByteString.copyFrom(buffer, 0, index))
                    .build()

                sender() ! (openFileMessage, sourceId)

                partition = partition + 1
            }
            parent ! FinishedFileOperation(CommandCode.OpenFile, path, sourceId)
            context.stop(self)
        case x =>
            logger.warning(s"what ? ${x}")

    }
}

case class IllegalCreateFileMessageException(path: String, message: String, sourceId: String) extends Exception(message)
case class IllegalOpenFileMessageException(path: String, message: String, sourceId: String) extends Exception(message)