package com.github.sioncheng.fs.act

import spray.json._
import DefaultJsonProtocol._

class Messages {

}


case class Leader()
case class Worker()
case class Lost()
case class Return()
case class Exit()
case class RegisterWorker(name: String, data: Array[Byte])
case class UnregisterWorker(name: String)
case class RegisterWorkers(names: Seq[String])

object FileOp {

    case class Status(isDir: Boolean)

    case class List(path: String)

    case class CreateDir(path: String)

    case class CreateFileOp(path: String, data: Array[Byte])

    case class Delete(path: String)

    case class File(path: String, data: Array[Byte])

    case class Directory(path: String, children: Seq[String])


    object StatusJsonProtocol extends DefaultJsonProtocol {
        implicit val statusFormat = jsonFormat1(Status)
    }

    def serializeStatus(s: Status): String = {
        import StatusJsonProtocol.statusFormat
        s.toJson.toString()
    }

    def deserializeStatus(s: String): Status = {
        import StatusJsonProtocol.statusFormat
        JsonParser(s).convertTo[Status]
    }
}