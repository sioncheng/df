package com.github.sioncheng.fs

import akka.actor.{ActorSystem, Props}
import com.github.sioncheng.cnf.{AppConfiguration, AppConfigurationLoader}
import com.github.sioncheng.fs.act.{MainActor, ZookeeperActor}
import org.slf4j.LoggerFactory

import scala.io.Source


object StartApp extends App {

    val logger = LoggerFactory.getLogger("StartApp")

    logger.info("fs app")


    val systemName = "com.github.sion.cheng.fs".replace(".","_")

    val actorSystem = ActorSystem(systemName)

    val mainActor = actorSystem.actorOf(Props[MainActor], "main")

    val zookeeperActorProps = Props.create(classOf[ZookeeperActor],
        mainActor,
        AppConfiguration(Source.fromResource("appconf.json").mkString))
    val zookeeperActor = actorSystem.actorOf(zookeeperActorProps, "zookeeper")



    io.StdIn.readLine()

    actorSystem.terminate()
}
