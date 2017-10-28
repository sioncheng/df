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


class FileActor(val appConf: AppConfiguration, val mainActor: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[FileActor])

    val createFileActors : mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()
    val createFileActorSource: mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()
    val creatingFilePathSet: mutable.HashSet[String] =
        new mutable.HashSet[String]()

    val openFileActors: mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()
    val openFileActorSource: mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()

    override def receive: Receive = {
        case fcm : FileCommandMessage =>
            logger.info(s"received file command ${fcm.fc.commandCode}")
            processFileCommand(fcm.fc, fcm.sourceId)
        case ffo: FinishedFileOperation =>
            ffo.code match {
                case CommandCode.CreateFile =>
                    removeCreateFileActor(ffo)
                case CommandCode.OpenFile =>
                    removeOpenFileActor(ffo)
            }
        case fcr: FileCommandResult =>
            fcr.commandCode match {
                case CommandCode.CreateFile =>
                    createFileActorSource.get(fcr.sourceId).head ! fcr
                case CommandCode.OpenFile =>
                    openFileActorSource.get(fcr.sourceId).head ! fcr
                case x =>
                    logger.error(s"cant deal with file command result ${x}")
            }
        case x =>
            logger.warning(s"received unknown command $x")
    }


    override val supervisorStrategy =
        OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
            case _: ArithmeticException      => Resume
            case _: NullPointerException     => Restart
            case e: IllegalCreateFileMessageException =>
                logger.error(s"e.getMessage ${e.getMessage}")
                mainActor ! FileOperationException(CommandCode.CreateFile, e.path, e.getMessage, e.sourceId)
                removeCreateFileActor(FinishedFileOperation(CommandCode.CreateFile, e.path, e.sourceId))
                Stop
            case e: IllegalOpenFileMessageException =>
                logger.error(s"e.getMessage ${e.getMessage}")
                mainActor ! FileOperationException(CommandCode.OpenFile, e.path, e.getMessage, e.sourceId)
                removeOpenFileActor(FinishedFileOperation(CommandCode.OpenFile, e.path, e.sourceId))
                Stop
            case x: Exception                =>
                logger.warning(s"escalate ${x}")
                Escalate
        }

    def processFileCommand(fc: FileCommand, sourceId: String): Unit = {
        val root = Utils.parseRoot(appConf.getString("fs-root").get)
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
                    createFileActors.put(createFileMessage.getPath, createFileActor)
                    creatingFilePathSet.add(createFileMessage.getPath)
                    createFileActorSource.put(sourceId, sender())
                }
                createFileActors.get(createFileMessage.getPath).head ! createFileMessage
            case CommandCode.DeleteFile =>
                val deleteFileMessage = DeleteFile.DeleteFileMessage.parseFrom(fc.data)
                val file = new File(root, deleteFileMessage.getPath)
                sender() ! FileCommandResult(CommandCode.DeleteFile, root, deleteFileMessage.getPath, file.delete(), sourceId, None)
            case CommandCode.FindFile =>
                val findFileMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                val file = new File(root, findFileMessage.getPath)
                sender() ! FileCommandResult(CommandCode.FindFile, root, findFileMessage.getPath, file.exists(), sourceId, None)
            case CommandCode.OpenFile =>
                val filePathMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                if (false == openFileActors.contains(sourceId)) {
                    val openFileMessage = FilePath.FilePathMessage.parseFrom(fc.data)
                    val props = Props.create(classOf[OpenFileActor],
                        root,
                        openFileMessage.getPath,
                        self,
                        sourceId)

                    val openFileActor = context.actorOf(props)
                    openFileActors.put(sourceId, openFileActor)
                    openFileActorSource.put(sourceId, sender())
                }
                openFileActors.get(sourceId).head ! filePathMessage
            case x =>
                logger.warning(s"what? ${x}")
        }
    }


    private def removeCreateFileActor(ffo: FinishedFileOperation): Unit = {
        val createFileActor = createFileActors.get(ffo.path).get
        context.stop(createFileActor)
        createFileActors.remove(ffo.path)
        createFileActorSource.remove(ffo.sourceId)
        creatingFilePathSet.remove(ffo.path)
    }

    private def removeOpenFileActor(ffo: FinishedFileOperation): Unit = {
        val openFileActor = openFileActors.get(ffo.path).get
        context.stop(openFileActor)
        openFileActors.remove(ffo.path)
        openFileActorSource.remove(ffo.sourceId)
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
                logger.error(message)
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
                    sender() ! FileCommandResult(CommandCode.CreateFile, root, path, false, sourceId, None)
                    parent ! IllegalCreateFileMessageException(path, e.getMessage, sourceId)
            }

            //
            expectPartition = expectPartition + 1

            if (expectPartition == createFileMessage.getPartitions) {
                logger.info(s"finished write file ${createFileMessage.getPath}")
                sender() ! FileCommandResult(CommandCode.CreateFile, root, path, true, sourceId, None)
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

                val data = OpenFile.OpenFileMessage.newBuilder()
                    .setPath(path)
                    .setContentLength(file.length())
                    .setPartitions(partitions.toInt)
                    .setPartitionNo(partition)
                    .setPartitionLength(index)
                    .setData(ByteString.copyFrom(buffer, 0, index))
                    .build()
                    .toByteArray

                sender() ! FileCommandResult(CommandCode.OpenFile, root, path,true, sourceId, Some(data))

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