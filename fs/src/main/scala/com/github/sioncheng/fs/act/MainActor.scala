package com.github.sioncheng.fs.act

import akka.actor.Actor
import akka.event.Logging

class MainActor extends Actor {

    val logger = Logging(context.system, classOf[MainActor])

    override def receive: Receive = {
        case _ @ Leader() =>
            logger.info("playing as a leader")
        case _ @ Worker() =>
            logger.info("playing as a worker")
        case _ @ Lost() =>
            logger.warning("lost, can't do anything")
        case _ @ Return() =>
            logger.warning("ha, i am back.")
        case x =>
            logger.info(s"what ? $x")
    }
}
