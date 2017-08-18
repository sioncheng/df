package com.github.sioncheng.df

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future

object StartApp extends App {

    println("df app")


    implicit val system = ActorSystem("fm")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val requestHandler: HttpRequest => HttpResponse = {
        case HttpRequest(HttpMethods.GET, Uri.Path("/"), _, _, _) =>
            println("response to get /")
            HttpResponse(entity = HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                "<html><body>Hello world!</body></html>"),
                headers = List(Connection("close")))
        case r: HttpRequest =>
            r.discardEntityBytes() // important to drain incoming HTTP Entity stream
            HttpResponse(404,
                entity = "Unknown resource!",
                headers = List(Connection("close")))
    }

    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
        Http().bind(interface = "localhost", port = 8080)

    val bindingFuture: Future[Http.ServerBinding] =
        serverSource.to(Sink.foreach{
            connection =>
                println(s"accepted new connection from ${connection.remoteAddress}")
                connection.handleWithSyncHandler(requestHandler)
        }).run()
}
