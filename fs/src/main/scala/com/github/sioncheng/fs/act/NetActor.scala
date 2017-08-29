package com.github.sioncheng.fs.act

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.stream.ActorMaterializer
import com.github.sioncheng.cnf.AppConfiguration

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
            val handler = system.actorOf(Props.create(classOf[ConnectionHandler], conn))
            conn ! Register(handler)
        case "hello" =>
            if (bound) {
                sender() ! "hello world"
            } else {
                sender() ! "what?"
            }
    }
}

class ConnectionHandler(conn: ActorRef) extends Actor {

    val logger = Logging(context.system, classOf[ConnectionHandler])

    implicit val system = context.system
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    override def receive: Receive = {
        case Received(data) =>
            logger.info(s"received ${data.utf8String}")
        case PeerClosed =>
            logger.info(s"peer closed $conn")
    }
}
