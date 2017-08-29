package com.github.sioncheng.fs.act

import akka.actor.Actor
import akka.event.Logging
import com.github.sioncheng.cnf.AppConfiguration

class FileActor(val appConf: AppConfiguration) extends Actor {

    val logger = Logging(context.system, classOf[FileActor])

    override def receive: Receive = {
        case x =>
            logger.info(s"received $x")
    }
}
