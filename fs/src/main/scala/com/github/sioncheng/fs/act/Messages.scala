package com.github.sioncheng.fs.act

class Messages {

}


case class Leader()
case class Worker()
case class Lost()
case class Return()
case class RegisterWorker(name: String, data: Array[Byte])
case class UnregisterWorker(name: String)
case class RegisterWorkers(names: Seq[String])