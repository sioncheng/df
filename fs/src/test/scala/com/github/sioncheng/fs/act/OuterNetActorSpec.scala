package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.sioncheng.cnf.AppConfigurationLoader
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class OuterNetActorSpec() extends TestKit(ActorSystem("NetActorSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    override def afterAll: Unit = {
        TestKit.shutdownActorSystem(system)
    }

    "NetActor" must {
        "bind success" in {
            val props = Props.create(classOf[OuterNetActor],
                AppConfigurationLoader.loadFromResourceFile("/appconf.json"),
                self)
            val na = system.actorOf(props)

            Thread.sleep(2000)

            na ! "hello"
            expectMsg("hello world")
        }
    }
}
