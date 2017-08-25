package com.github.sioncheng.fs.act

import java.net.InetSocketAddress

import akka.actor.Actor
import akka.event.Logging
import com.loopfor.zookeeper.{ACL, Configuration, EphemeralSequential, NodeEvent, Persistent, StateEvent, Zookeeper}
import org.apache.zookeeper.KeeperException.NoNodeException
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

class ZookeeperActor extends Actor {

    val MASTER = "/master"
    val WORKER = "/workers"

    val logger = Logging(context.system, classOf[ZookeeperActor])

    override def preStart(): Unit = {
        val servers = List(new InetSocketAddress("127.0.0.1", 2181))
        val conf = Configuration(servers)

        val zk = Zookeeper(conf)


        import scala.concurrent.ExecutionContext.Implicits.global
        val asClient = zk.async

        createIfNotExists(WORKER,())

        createIfNotExists(MASTER, createMasterFlag())


        def createIfNotExists(path: String, f: => Unit): Unit = {
            asClient.exists(path).onComplete {
                case Success(v) => {
                    logger.info(s"$path exists $v")
                    f
                }
                case Failure(e) => {
                    if (e.isInstanceOf[NoNodeException]) {
                        logger.info(s"no $path then create it")
                        asClient.create(path,
                            "".getBytes() ,
                            ACL.AnyoneAll,
                            Persistent).onComplete {
                            case Success(v) =>
                                logger.info(s"create $path success $v")
                                f
                            case Failure(e) =>
                                logger.error(s"create $path failure", e)
                        }

                    } else {
                        logger.error(s"check $path failure", e)
                    }
                }
            }
        }

        def createMasterFlag(): Unit = {
            asClient.create(s"$MASTER/m_",
                "".getBytes(),
                ACL.AnyoneAll,
                EphemeralSequential).onComplete {
                case Success(value) => {
                    logger.info(s"success $value")
                    checkLeader(value)
                }
                case Failure(e) => {
                    logger.error("create master flag error", e)
                }
            }
        }

        def checkLeader(value:String): Unit = {
            asClient.children(MASTER).onComplete {
                case Success(vv) => {
                    logger.info(s"children $vv")
                    val num = value.substring(s"$MASTER/".length)
                    val children = vv._1.sorted
                    if (children.isEmpty || children.head.equalsIgnoreCase(num)) {
                        logger.info("i am the leader")
                    } else {
                        logger.info("oops, i am not the leader.")

                        asClient.watch {
                            case e: NodeEvent => {
                                logger.info(s"node event $e")
                                checkLeader(value)
                            }
                            case e: StateEvent => logger.info(s"state event $e")
                        }.children(MASTER).onComplete {
                            case Success(vvv) =>
                                logger.info(s"$vvv")
                            case Failure(eee) =>
                                logger.error(s"watch $MASTER err", eee)
                        }
                    }
                }
                case Failure(ee) => {
                    logger.error("failure", ee)
                }
            }
        }

    }

    override def receive: Receive = {
        case x => logger.info(s"receive $x")
    }
}
