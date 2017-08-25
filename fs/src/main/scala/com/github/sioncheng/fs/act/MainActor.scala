package com.github.sioncheng.fs.act

import akka.actor.Actor

class MainActor extends Actor {

    override def receive: Receive = {
        case x => println(x)
    }
}
