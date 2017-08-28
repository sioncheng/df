package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class NetActorSpec() extends TestKit(ActorSystem("NetActorSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    override def afterAll: Unit = {
        TestKit.shutdownActorSystem(system)
    }

    "NetActor" must {
        "bind success" in {
            val listenOn = "0.0.0.0:6000"
            val na = system.actorOf(Props.create(classOf[NetActor], listenOn, self))

            na ! "hello"
            expectMsg("hello world")
        }
    }
}
