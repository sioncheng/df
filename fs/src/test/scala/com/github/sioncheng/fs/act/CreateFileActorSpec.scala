package com.github.sioncheng.fs.act

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.sioncheng.prtl.{CommandCode, FileCommand}
import com.github.sioncheng.prtl.outer.CreateFile
import com.google.protobuf.ByteString
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class MySupervisor extends Actor {

    val createFile = context.actorOf(Props.create(classOf[CreateFileActor], "/fs-root", "a.txt"))

    var testProb : ActorRef = null

    override val supervisorStrategy =
        OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
            case _: ArithmeticException      => Resume
            case _: NullPointerException     => Restart
            case e: IllegalCreateFileMessageException =>
                println(e.getMessage)
                testProb ! "stop"
                Stop
            case _: Exception                => Escalate
        }

    override def receive: Receive = {
        case x =>
            testProb = sender()
            createFile ! x
    }
}

class CreateFileActorSpec() extends TestKit(ActorSystem("create-file-actor-spec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    override def afterAll(): Unit = {
        TestKit.shutdownActorSystem(system)
    }


    "create file actor " should {
        "failure if meet unexpected partition number" in {


            val props = Props.create(classOf[MySupervisor])
            val createFileActor = system.actorOf(props)

            val createFileMessage = CreateFile.CreateFileMessage.newBuilder()
                .setPath("a.txt")
                .setContentLength(10130)
                .setPartitions(3)
                .setPartitionNo(1)
                .setPartitionLength(10)
                .setData(ByteString.copyFrom("abcdefghij".getBytes))
                .build()


            createFileActor ! createFileMessage

            Thread.sleep(2000)

            expectMsg("stop")

        }
    }
}
