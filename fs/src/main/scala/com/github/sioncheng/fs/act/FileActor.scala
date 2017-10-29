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
            processFileCommand(fcm)
        case ffo: FinishedFileOperation =>
            ffo.code match {
                case CommandCode.CreateFile =>
                    removeCreateFileActor(ffo)
                case CommandCode.OpenFile =>
                    removeOpenFileActor(ffo)
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

    def processFileCommand(fcm: FileCommandMessage): Unit = {
        val root = Utils.parseRoot(appConf.getString("fs-root").get)
        fcm.fc.commandCode match {
            case CommandCode.CreateFile =>
                println(s"create file ${fcm}")
                val createFileMessage = CreateFile.CreateFileMessage.parseFrom(fcm.fc.data)
                if (false == creatingFilePathSet.contains(createFileMessage.getPath)) {
                    val props = Props.create(classOf[CreateFileActor],
                        root,
                        createFileMessage.getPath,
                        self,
                        fcm.sourceId)
                    val createFileActor = context.actorOf(props)
                    createFileActors.put(createFileMessage.getPath, createFileActor)
                    creatingFilePathSet.add(createFileMessage.getPath)
                    createFileActorSource.put(fcm.sourceId, sender())
                }
                createFileActors.get(createFileMessage.getPath).head ! createFileMessage
            case CommandCode.CreateFileResult =>
                createFileActorSource.get(fcm.sourceId).head ! fcm
            case CommandCode.DeleteFile =>
                val deleteFileMessage = DeleteFile.DeleteFileMessage.parseFrom(fcm.fc.data)
                val file = new File(root, deleteFileMessage.getPath)
                val deleteFileResult = DeleteFileResult.DeleteFileResultMessage.newBuilder()
                        .setPath(deleteFileMessage.getPath)
                        .setSuccess(file.delete())
                        .build()
                        .toByteArray
                sender() ! FileCommandMessage(FileCommand(0, CommandCode.DeleteFileResult, deleteFileResult), fcm.sourceId)
            case CommandCode.DeleteFileResult =>
                //
            case CommandCode.FindFile =>
                val findFileMessage = FindFile.FindFileMessage.parseFrom(fcm.fc.data)
                val file = new File(root, findFileMessage.getPath)
                val findFileResult = FindFileResult.FindFileResultMessage.newBuilder()
                        .setPath(findFileMessage.getPath)
                        .setSuccess(file.exists())
                        .build()
                        .toByteArray
                sender() ! FileCommandMessage(FileCommand(0, CommandCode.FindFileResult, findFileResult), fcm.sourceId)
            case CommandCode.FindFileResult =>
                //todo
            case CommandCode.OpenFile =>
                val openFileMessage = OpenFile.OpenFileMessage.parseFrom(fcm.fc.data)
                if (false == openFileActors.contains(fcm.sourceId)) {
                    val props = Props.create(classOf[OpenFileActor],
                        root,
                        openFileMessage.getPath,
                        self,
                        fcm.sourceId)

                    val openFileActor = context.actorOf(props)
                    openFileActors.put(fcm.sourceId, openFileActor)
                    openFileActorSource.put(fcm.sourceId, sender())
                }
                openFileActors.get(fcm.sourceId).head ! openFileMessage
            case CommandCode.OpenFileResult =>
                openFileActorSource.get(fcm.sourceId).head ! fcm
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

    var f : FileOutputStream = null

    override def postStop(): Unit = {
        closeFile()
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
                f = new FileOutputStream(file)
            }
            println(s"create file ${createFileMessage.getData.toStringUtf8}")
            val bf = createFileMessage.getData.toByteArray
            try {
                f.write(bf)
            } catch {
                case e: IOException =>
                    logger.error(s"create ${path} error ${e.getMessage}")

                    val createFileResult = CreateFileResult.CreateFileResultMessage.newBuilder()
                            .setPath(path)
                            .setSuccess(false)
                            .build()
                            .toByteArray
                    val fileCommand = FileCommand(0, CommandCode.CreateFileResult, createFileResult)
                    sender() ! FileCommandMessage(fileCommand, sourceId)
                    parent ! IllegalCreateFileMessageException(path, e.getMessage, sourceId)
            }

            //
            expectPartition = expectPartition + 1

            if (expectPartition == createFileMessage.getPartitions) {
                logger.info(s"finished write file ${createFileMessage.getPath}")
                closeFile()
                val createFileResult = CreateFileResult.CreateFileResultMessage.newBuilder()
                        .setPath(path)
                        .setSuccess(true)
                        .build()
                        .toByteArray
                val fileCommand = FileCommand(0, CommandCode.CreateFileResult, createFileResult)
                sender() ! FileCommandMessage(fileCommand, sourceId)
                parent ! FinishedFileOperation(CommandCode.CreateFile, path, sourceId)
            }
        case x =>
            logger.warning(s"what ? ${x}")
    }

    private def closeFile(): Unit = {
        if (f != null) {
            f.close()
            f = null
        }
    }
}

class OpenFileActor(root: String, path: String, parent: ActorRef, sourceId: String) extends Actor {
    val blockSize = 1024
    val logger = Logging(context.system, classOf[OpenFileActor])

    override def receive: Receive = {
        case _ : OpenFile.OpenFileMessage =>
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

                val data = OpenFileResult.OpenFileResultMessage.newBuilder()
                    .setPath(path)
                    .setSuccess(true)
                    .setContentLength(file.length())
                    .setPartitions(partitions.toInt)
                    .setPartitionNo(partition)
                    .setPartitionLength(index)
                    .setData(ByteString.copyFrom(buffer, 0, index))
                    .build()
                    .toByteArray

                sender() ! FileCommandMessage(FileCommand(0, CommandCode.OpenFileResult, data), sourceId)

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