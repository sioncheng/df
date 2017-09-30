package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.io.Tcp.Received
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.ByteString
import com.github.sioncheng.prtl.{CommandCode, CommandSerializer, FileCommand}
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

            val createFile = FileCommand(CommandCode.CreateFile, commandData)

            val netBytes = CommandSerializer.toBytes(createFile)

            connectonHandler ! Received(ByteString.fromArray(netBytes))

            val expectCommand = ReceivedCommand(createFile)

            Thread.sleep(1000)

            expectMsgPF() {
                case x @ ReceivedCommand(createFile) =>
                    expectCommand
            }
        }
    }
}
