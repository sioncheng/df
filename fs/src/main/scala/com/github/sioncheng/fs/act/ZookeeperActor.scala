package com.github.sioncheng.fs.act

import java.net.InetSocketAddress

import akka.actor.Actor
import com.loopfor.zookeeper.{ACL,
    Configuration, EphemeralSequential, NodeEvent, StateEvent, Zookeeper}

import scala.util.{Failure, Success}

class ZookeeperActor extends Actor {

    override def preStart(): Unit = {
        val servers = List(new InetSocketAddress("127.0.0.1", 2181))
        val conf = Configuration(servers)

        val zk = Zookeeper(conf)


        import scala.concurrent.ExecutionContext.Implicits.global
        val asClient = zk.async


        asClient.create("/root/guid-n_",
            "".getBytes(),
            ACL.AnyoneAll,
            EphemeralSequential ).onComplete {
                case Success(value) =>  {
                    println(s"success $value")
                    checkLeader(value)
                }
                case Failure(e) => {
                    println("failure")
                    e.printStackTrace()
                }
            }

        def checkLeader(value:String): Unit = {
            asClient.children("/root").onComplete {
                case Success(vv) => {
                    println(s"children $vv")
                    val num = value.substring("/root/".length)
                    println(num)
                    val children = vv._1.sorted
                    println(children.head)
                    if (children.head.equalsIgnoreCase(num)) {
                        println("i am the leader")
                    } else {
                        println("oops, i am not the leader.")

                        asClient.watch {
                            case e: NodeEvent => {
                                println(s"node event $e")
                                checkLeader(value)
                            }
                            case e: StateEvent => println(s"state event $e")
                        }.children("/root").onComplete {
                            case Success(vvv) => println(vvv)
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

    }

    override def receive: Receive = {
        case x => println(x)
    }
}
