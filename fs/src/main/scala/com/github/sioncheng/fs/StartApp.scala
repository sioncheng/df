package com.github.sioncheng.fs

import akka.actor.{ActorSystem, Props}
import com.github.sioncheng.fs.act.{MainActor, ZookeeperActor}


object StartApp extends App {

    println("fs app")

    val systemName = "com.github.sion.cheng.fs".replace(".","_")

    val actorSystem = ActorSystem(systemName)

    val mainActor = actorSystem.actorOf(Props[MainActor], "main")

    val electionActor = actorSystem.actorOf(Props.create(classOf[ZookeeperActor], mainActor), "zookeeper")

    io.StdIn.readLine()

    actorSystem.terminate()
}
