package com.github.sioncheng.fs.act

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.sioncheng.cnf.AppConfiguration
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._

import scala.io.Source

class ZookeeperActorSpec() extends TestKit(ActorSystem("zookeeperSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    var zookeeperActor: ActorRef = null

    override protected def beforeAll(): Unit = {
        println("before all")
        val zookeeperActorProps = Props.create(classOf[ZookeeperActor],
            self,
            AppConfiguration(Source.fromResource("appconf.json").mkString))
        zookeeperActor = system.actorOf(zookeeperActorProps, "zookeeper")
    }

    override def afterAll: Unit = {
        TestKit.shutdownActorSystem(system)
    }

    "zookeeper actor " must {

        "init" in {
            receiveOne(3.seconds)
        }

        "list /fs-root" in {

            zookeeperActor ! FileOp.List("/fs-root")

            Thread.sleep(3000)

            expectMsg(FileOp.Directory("/fs-root", List()))
        }
    }
}
