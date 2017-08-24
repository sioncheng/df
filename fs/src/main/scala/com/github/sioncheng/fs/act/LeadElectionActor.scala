package com.github.sioncheng.fs.act

import java.net.InetSocketAddress

import akka.actor.Actor
import com.loopfor.zookeeper.{ACL, Configuration, EphemeralSequential, Zookeeper}

import scala.util.{Failure, Success}

class LeadElectionActor extends Actor {

    override def preStart(): Unit = {
        val servers = List(new InetSocketAddress("127.0.0.1", 2181))
        val conf = Configuration(servers)

        val zk = Zookeeper(conf)


        import scala.concurrent.ExecutionContext.Implicits.global
        val asClient = zk.async


        asClient.create("/root/guid-n_",
            "".getBytes(),
            ACL.AnyoneAll,
            EphemeralSequential ).onComplete(f => {
            f match {
                case Success(value) =>  {
                    println(s"success $value")

                    asClient.children("/root").onComplete {
                        case Success(vv) => {
                            println(s"children $vv")
                            val num = value.substring("/root/".length)
                            println(num)
                            println(vv._1.head)
                            if (vv._1.head.equalsIgnoreCase(num)) {
                                println("i am the leader")
                            } else {
                                println("oops, i am not the leader.")

                                val watched = vv._1.drop(vv._1.length -2).head
                                println(watched)

                                val fullWatched = "/root/" + watched
                                println(fullWatched)
                                asClient.exists(fullWatched).onComplete {
                                    case Success(vvv) => println(s"watched $vvv")
                                    case Failure(eee) => eee.printStackTrace()
                                }
                            }
                        }
                        case Failure(ee) => {
                            println("failure")
                            ee.printStackTrace()
                        }
                    }
                }
                case Failure(e) => {
                    println("failure")
                    e.printStackTrace()
                }
            }
        })


    }

    override def receive: Receive = {
        case x => println(x)
    }
}
