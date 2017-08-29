package com.github.sioncheng.fs

import akka.actor.{ActorSystem, Props}
import com.github.sioncheng.cnf.AppConfigurationLoader
import com.github.sioncheng.fs.act.{MainActor, ZookeeperActor}


object StartApp extends App {

    println("fs app")

    val systemName = "com.github.sion.cheng.fs".replace(".","_")

    val actorSystem = ActorSystem(systemName)

    val mainActor = actorSystem.actorOf(Props[MainActor], "main")

    val zookeeperActorProps = Props.create(classOf[ZookeeperActor],
        mainActor,
        AppConfigurationLoader.loadFromResourceFile("/appconf.json"))
    val zookeeperActor = actorSystem.actorOf(zookeeperActorProps, "zookeeper")

    io.StdIn.readLine()

    actorSystem.terminate()
}
