package com.github.sioncheng.fs.act

import akka.actor.Actor
import akka.event.Logging
import com.github.sioncheng.cnf.AppConfiguration
import com.github.sioncheng.prtl.FileCommand

class FileActor(val appConf: AppConfiguration) extends Actor {

    val logger = Logging(context.system, classOf[FileActor])

    override def receive: Receive = {
        case fc : FileCommand =>
            logger.info(s"received file command ${fc.commandCode}")
        case x =>
            logger.info(s"received $x")
    }
}
