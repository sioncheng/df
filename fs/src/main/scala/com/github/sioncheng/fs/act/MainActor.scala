package com.github.sioncheng.fs.act

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import com.github.sioncheng.cnf.AppConfiguration

import scala.collection.mutable


trait MainStatus
case object Unknown extends MainStatus
case object Leading extends MainStatus
case object Working extends MainStatus
case class Losing(val prev: MainStatus) extends MainStatus

class MainActor(appConf: AppConfiguration) extends Actor {

    val logger = Logging(context.system, classOf[MainActor])

    var status: MainStatus = Unknown

    val workers: scala.collection.mutable.HashMap[String, Array[Byte]] =
        new mutable.HashMap[String, Array[Byte]]()

    var outerNetActor: ActorRef = null
    var fileActor: ActorRef = null

    override def receive: Receive = {
        //cluster thing
        case _ @ Leader() =>
            status = Leading
            workers.clear()
            logger.info(s"my status $status")
        case _ @ Worker() =>
            status = Working
            workers.clear()
            checkAndStartOuterNetActor()
            checkAndStartFileActor()
            logger.info(s"my status $status")
        case _ @ Lost() =>
            status = Losing(status)
            logger.warning(s"my status $status")
        case _ @ Return() =>
            status = status.asInstanceOf[Losing].prev
            logger.warning(s"ha, i am back. $status")
        case _ @ RegisterWorkers(names) =>
            logger.info(s"register workers $names")
            val preWorkers = workers.keySet
            preWorkers.foreach(k => {
                if (!names.contains(k)) {
                    workers.-=(k)
                    logger.info(s"remove worker $k")
                }
            })
            val remainWorkers = workers.keySet
            remainWorkers.foreach(n => println(s"remain worker $n"))
            names.foreach(name => {
                if (!remainWorkers.contains(name)) {
                    logger.info(s"new worker $name")
                }
            })

        case _ @ RegisterWorker(name, data) =>
            logger.info(s"register worker $name")
            workers.+=((name, data))
        case _ @ UnregisterWorker(name) =>
            logger.info(s"un-register worker $name")
            workers.-(name)
        // command from network thing
        case rc : ReceivedCommand =>
            logger.info(s"received command ${rc.command.commandCode}")
            if (status == Leading) {
                fileActor ! FileCommandMessage(rc.command, rc.clientId)
            } else {
                logger.warning(s"not leading , cant process ${rc.command.commandCode}")
            }
        // file operation thing
        case x =>
            logger.info(s"what ? $x")
    }

    private def checkAndStartOuterNetActor(): Unit = {
        if (outerNetActor != null) {
            return
        }
        val props = Props.create(classOf[OuterNetActor], appConf, self)
        outerNetActor = context.actorOf(props)
    }

    private def checkAndStartFileActor(): Unit = {
        if (fileActor != null) {
            return
        }
        val props = Props.create(classOf[FileActor], appConf, self)
        fileActor = context.actorOf(props)
    }
}


