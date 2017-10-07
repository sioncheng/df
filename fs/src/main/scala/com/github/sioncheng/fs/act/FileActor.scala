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

case class FileOperationException(code: Int, path: String, message: String)
case class FinishedFileOperation(code: Int, path: String)

class FileActor(val appConf: AppConfiguration, val mainActor: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[FileActor])
    val createFileActors : scala.collection.mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()

    override def receive: Receive = {
        case fc : FileCommand =>
            logger.info(s"received file command ${fc.commandCode}")
            processFileCommand(fc)
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
                mainActor ! FileOperationException(CommandCode.CreateFile, e.path, e.getMessage)
                removeCreateFileActor(e.path)
                Stop
            case e: IllegalOpenFileMessageException =>
                logger.error(s"e.getMessage ${e.getMessage}")
                mainActor ! FileOperationException(CommandCode.OpenFile, e.path, e.getMessage)
                Stop
            case x: Exception                =>
                logger.warning(s"escalate ${x}")
                Escalate
        }

    def processFileCommand(fc: FileCommand): Unit = {
        val root = appConf.getString("fs-root").get
        fc.commandCode match {
            case CommandCode.CreateFile =>
                val createFileMessage = CreateFile.CreateFileMessage.parseFrom(fc.data)
                if (false == createFileActors.contains(createFileMessage.getPath)) {
                    val props = Props.create(classOf[CreateFileActor],
                        root,
                        createFileMessage.getPath,
                        self)
                    val createFileActor = context.actorOf(props)
                    createFileActors.put(createFileMessage.getPath, createFileActor)
                }
                createFileActors.get(createFileMessage.getPath).head forward createFileMessage
            case CommandCode.DeleteFile =>
                val deleteFileMessage = DeleteFile.DeleteFileMessage.parseFrom(fc.data)
                val file = new File(root, deleteFileMessage.getPath)
                sender() ! DeleteFileResult(root, deleteFileMessage.getPath, file.delete())
            case CommandCode.FindFile =>
                val findFileMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                val file = new File(root, findFileMessage.getPath)
                sender() ! FindFileResult(root, findFileMessage.getPath, file.exists())
            case CommandCode.OpenFile =>
                val openFileMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                val props = Props.create(classOf[OpenFileActor],
                    root,
                    openFileMessage.getPath,
                    self)
                val openFileActor = context.actorOf(props)
                openFileActor forward  openFileMessage
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


class CreateFileActor(root: String, path: String, parent: ActorRef) extends Actor {

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
                throw new IllegalCreateFileMessageException(path, message)
            }

            if (f == null) {
                val file = new File(root, path)
                if (file.exists()) {
                    val message = s"${root}${path} exists"
                    throw new IllegalCreateFileMessageException(path, message)
                }
                f = (new FileOutputStream(file, true)).getChannel
            }
            val bf = ByteBuffer.wrap(createFileMessage.getData.toByteArray)
            var remain = bf.limit()
            var index = 0
            while(remain > 0) {
                val n = f.write(bf)
                index = index + n
                remain = remain - n
            }

            //
            expectPartition = expectPartition + 1

            if (expectPartition == createFileMessage.getPartitions) {
                logger.info(s"finished write file ${createFileMessage.getPath}")

                sender() ! CreateFileResult(root, path, true)

                parent ! FinishedFileOperation(CommandCode.CreateFile, path)
            }
        case x =>
            logger.warning(s"what ? ${x}")
    }
}

class OpenFileActor(root: String, path: String, parent: ActorRef) extends Actor {
    val blockSize = 1024
    val logger = Logging(context.system, classOf[OpenFileActor])

    override def receive: Receive = {
        case _ : FilePath.FilePathMessage =>
            val file = new File(root, path)
            if (file.exists() == false) {
                throw new IllegalOpenFileMessageException(path, "file does not exist")
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

                sender() ! openFileMessage

                partition = partition + 1
            }
            parent ! FinishedFileOperation(CommandCode.OpenFile, path)
            context.stop(self)
        case x =>
            logger.warning(s"what ? ${x}")

    }
}

case class IllegalCreateFileMessageException(path: String, message: String) extends Exception(message)
case class IllegalOpenFileMessageException(path: String, message: String) extends Exception(message)