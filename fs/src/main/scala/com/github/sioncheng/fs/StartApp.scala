package com.github.sioncheng.fs

import akka.actor.{ActorSystem, Props}
import com.github.sioncheng.fs.act.ZookeeperActor


object StartApp extends App {

    println("fs app")

    val systemName = "com.github.sioncheng.fs".replace(".","_")

    val actorSystem = ActorSystem(systemName)

    val electionActor = actorSystem.actorOf(Props[ZookeeperActor])

    io.StdIn.readLine()

    actorSystem.terminate()
}
