package com.github.sioncheng.fs.act

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.sioncheng.cnf.AppConfiguration
import com.github.sioncheng.prtl.{CommandSerializer, FileCommand}

case class ReceivedCommand(command: FileCommand)


class NetActor(val appConf: AppConfiguration, val mainActor: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[NetActor])

    var bound: Boolean = false

    implicit val system = context.system
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    override def preStart(): Unit = {
        val listenOn = appConf.getString("listen-on").getOrElse("")
        val ipAndPort = listenOn.split(":")
        val serverAddress = new InetSocketAddress(ipAndPort.head, ipAndPort.last.toInt);
        IO(Tcp) ! Bind(self, serverAddress)
    }

    override def postRestart(reason: Throwable): Unit = {
        logger.error("post restart", reason)
        context.stop(self)
    }


    override def receive: Receive = {
        case Bound(localAddress) =>
            logger.info(s"server is bound to $localAddress")
            bound = true
        case CommandFailed(cmd) =>
            logger.error(s"command failed $cmd")
            context.stop(self)
        case Connected(remote, local) =>
            logger.info(s"connected $remote $local")
            val conn = sender()
            val handler = system.actorOf(Props.create(classOf[ConnectionHandler], conn, self))
            conn ! Register(handler)
        case receivedCommand : ReceivedCommand =>
            logger.info(s"received command ${receivedCommand.command.commandCode}")
            mainActor ! receivedCommand
        case "hello" =>
            if (bound) {
                sender() ! "hello world"
            } else {
                sender() ! "what?"
            }
    }
}

class ConnectionHandler(conn: ActorRef, server: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[ConnectionHandler])

    val receivedBytes = java.nio.ByteBuffer.allocate(10240)

    implicit val system = context.system
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    override def receive: Receive = {
        case Received(data) =>
            logger.info(s"received ${data.utf8String}")
            parseReceivedData(data)
        case PeerClosed =>
            logger.info(s"peer closed $conn")
    }

    def parseReceivedData(data : ByteString): Unit = {
        //load data
        var sourceIndex: Int = 0
        var shouldLoopCopy: Boolean = true
        while(shouldLoopCopy) {
            if (10240 - receivedBytes.position() < data.length) {
                receivedBytes.compact()
            }
            val n = data.copyToBuffer(receivedBytes)
            sourceIndex = sourceIndex + n

            receivedBytes.flip() //to read
            val backBytes = receivedBytes.array()
            var size = receivedBytes.limit()
            var parsedIndex = receivedBytes.position()
            var shouldLoopParse: Boolean = true
            while(shouldLoopParse) {
                val parsedCommand = CommandSerializer.parseFrom(backBytes, parsedIndex, size)
                if (parsedCommand.isEmpty || parsedCommand.head._1.commandCode == 0) {
                    shouldLoopParse = false
                    shouldLoopCopy = false
                } else {
                    server ! ReceivedCommand(parsedCommand.head._1)
                    parsedIndex = parsedIndex + parsedCommand.head._2
                    size = size - parsedCommand.head._2
                }
            }
            receivedBytes.compact() // to write

            //
            if (sourceIndex == data.length) {
                shouldLoopCopy = false
            }
        }

    }
}
