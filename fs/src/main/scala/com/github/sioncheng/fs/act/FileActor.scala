package com.github.sioncheng.fs.act

import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.event.Logging
import com.github.sioncheng.cnf.AppConfiguration
import com.github.sioncheng.prtl.{CommandCode, FileCommand}
import com.github.sioncheng.prtl.outer.CreateFile

import scala.collection.mutable
import scala.concurrent.duration._

case class FileOperationException(code: Int, path: String)

class FileActor(val appConf: AppConfiguration, val mainActor: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[FileActor])
    val createFileActors : scala.collection.mutable.HashMap[String, ActorRef] =
        new mutable.HashMap[String, ActorRef]()

    override def receive: Receive = {
        case fc : FileCommand =>
            logger.info(s"received file command ${fc.commandCode}")
            processFileCommand(fc)
        case x =>
            logger.info(s"received $x")
    }


    override val supervisorStrategy =
        OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
            case _: ArithmeticException      => Resume
            case _: NullPointerException     => Restart
            case e: IllegalCreateFileMessageException =>
                logger.error(s"e.getMessage ${e.getMessage}")
                mainActor ! FileOperationException(CommandCode.CreateFile, e.getPath)
                Stop
            case x: Exception                =>
                logger.warning(s"escalate ${x}")
                Escalate
        }

    def processFileCommand(fc: FileCommand): Unit = {
        fc.commandCode match {
            case CommandCode.CreateFile =>
                val createFileMessage = CreateFile.CreateFileMessage.parseFrom(fc.data)
                if (false == createFileActors.contains(createFileMessage.getPath)) {
                    val props = Props.create(classOf[CreateFileActor],
                        appConf.getString("fs-root").get,
                        createFileMessage.getPath)
                    val createFileActor = context.actorOf(props)
                    createFileActors.put(createFileMessage.getPath, createFileActor)
                }
                createFileActors.get(createFileMessage.getPath).head ! createFileMessage
            case x =>
                logger.warning(s"what? ${x}")
        }
    }

}


class CreateFileActor(root: String, path: String) extends Actor {

    val logger = Logging(context.system, classOf[CreateFileActor])

    var expectPartition: Int = 0

    override def postRestart(reason: Throwable): Unit = context.stop(self)

    override def receive: Receive = {
        case createFileMessage: CreateFile.CreateFileMessage =>
            if (createFileMessage.getPartitionNo() != expectPartition) {
                val message = s"expect ${expectPartition} but ${createFileMessage.getPartitionNo}"
                throw new IllegalCreateFileMessageException(path, message)
            }


            //
            expectPartition = expectPartition + 1
        case x =>
            println(s"what ? ${x}")
    }
}

class IllegalCreateFileMessageException(path: String, message: String) extends Exception {
    def getPath: String = path
    override def getMessage: String = message
}