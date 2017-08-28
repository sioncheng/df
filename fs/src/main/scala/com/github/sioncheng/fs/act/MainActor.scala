package com.github.sioncheng.fs.act

import akka.actor.Actor
import akka.event.Logging


trait MainStatus
case object Unknown extends MainStatus
case object Leading extends MainStatus
case object Working extends MainStatus
case class Losing(val prev: MainStatus) extends MainStatus

class MainActor extends Actor {

    val logger = Logging(context.system, classOf[MainActor])

    var status: MainStatus = Unknown

    override def receive: Receive = {
        case _ @ Leader() =>
            status = Leading
            logger.info(s"my status $status")
        case _ @ Worker() =>
            status = Working
            logger.info(s"my status $status")
        case _ @ Lost() =>
            status = Losing(status)
            logger.warning(s"my status $status")
        case _ @ Return() =>
            status = status.asInstanceOf[Losing].prev
            logger.warning(s"ha, i am back. $status")
        case x =>
            logger.info(s"what ? $x")
    }
}


