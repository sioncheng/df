package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.io.Tcp.Received
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.ByteString
import com.github.sioncheng.prtl.{CommandCode, FileCommandSerializer, FileCommand}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class ConnectionHandlerSpec() extends TestKit(ActorSystem("ConnectionHandlerSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    override def afterAll: Unit = {
        TestKit.shutdownActorSystem(system)
    }

    "connection handler " must {
        "be able to parse file command" in {

            val props = Props.create(classOf[ConnectionHandler], self, self)
            val connectonHandler = system.actorOf(props)

            val commandData = "abcdefgh".getBytes

            val createFile = FileCommand(1, CommandCode.CreateFile, commandData)

            val netBytes = FileCommandSerializer.toBytes(createFile)

            connectonHandler ! Received(ByteString.fromArray(netBytes, 0, 5))
            connectonHandler ! Received(ByteString.fromArray(netBytes, 5, netBytes.length - 5))

            val expectCommand = ReceivedCommand(createFile, "123")

            Thread.sleep(1000)

            expectMsgPF() {
                case _ @ ReceivedCommand(`createFile`, "123") =>
                    expectCommand
            }
        }
    }
}
