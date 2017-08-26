package com.github.sioncheng.fs.act

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.loopfor.zookeeper.{ACL, AsynchronousZookeeper, Configuration, Connected, Disconnected, Ephemeral, EphemeralSequential, Expired, NodeEvent, Persistent, Session, StateEvent, Status, Zookeeper}
import org.apache.zookeeper.KeeperException.NoNodeException

import scala.util.{Failure, Success}

class ZookeeperActor(val mainActor: ActorRef) extends Actor {

    val MASTER = "/master"
    val WORKER = "/workers"

    val logger = Logging(context.system, classOf[ZookeeperActor])

    var zk: Zookeeper = null
    var asClient: AsynchronousZookeeper = null
    var session: Session  = null
    val servers = List(new InetSocketAddress("127.0.0.1", 2181))
    val conf = Configuration(servers).withWatcher((se,s) => {
        logger.info(s"connection status and session $se, $s")
        se match  {
            case Connected =>
                asClient = zk.async
                if (session == null) {
                    session = s
                    initRole()
                } else {
                    if (session.credential.id.equals(s.credential.id)) {
                        mainActor ! Return()
                    }
                }
        }
    }).build()

    zk = Zookeeper(conf)
    import scala.concurrent.ExecutionContext.Implicits.global

    var isMaster: Boolean = false

    def initRole(): Unit = {

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
                    asClient.children(MASTER).onComplete {
                        case Success(vv) => {
                            logger.info(s"children $vv")
                            checkLeader(value, vv)
                        }
                        case Failure(ee) => {
                            logger.error("failure", ee)
                        }
                    }
                }
                case Failure(e) => {
                    logger.error("create master flag error", e)
                }
            }
        }



    }

    override def receive: Receive = {
        case x => logger.info(s"receive $x")
    }

    def checkLeader(value: String, vv: (Seq[String], Status)): Unit = {
        val num = value.substring(s"$MASTER/".length)
        val children = vv._1.sorted
        if (children.isEmpty || children.head.equalsIgnoreCase(num)) {
            logger.info("i am the leader")
            isMaster = true
            unregisterWorkers(num)
            mainActor ! Leader()
        } else {
            logger.info("oops, i am not the leader.")
            isMaster = false
            registerWorkers(num)
            mainActor ! Worker()
        }

        watchSession(value)
    }

    def registerWorkers(name: String): Unit = {
        asClient.create(s"$WORKER/$name", "".getBytes(), ACL.AnyoneAll, Ephemeral).onComplete {
            case Success(v) =>
                logger.info(s"became worker $name $v")
            case Failure(e) =>
                logger.error("became worker $name failure", e)
        }
    }

    def unregisterWorkers(name: String): Unit = {
        var exists: Boolean = false
        val path = s"$WORKER/name"
        asClient.exists(path).onComplete {
            case Success(v) =>
                logger.info(s"exists $path $v")
                exists = true
            case Failure(e) =>
                logger.error(s"doesn't exists $path", e)
        }
        if (exists) {
            asClient.delete(s"$WORKER/$name", None).onComplete {
                case Success(v) =>
                    logger.info(s"un-became worker $v")
                case Failure(e) =>
                    logger.error(s"un-became worker $name failure", e)
            }
        }
    }

    def watchSession(value: String): Unit = {
        asClient.watch {
            case Disconnected =>
                logger.warning("disconnected")
                mainActor ! Lost()
                //preStart()
            case Expired =>
                logger.warning("expired")
                mainActor ! Lost()
                //preStart()
            case Connected =>
                logger.warning("connected")
                //initRole()
            case e: StateEvent =>
                logger.info(s"state event $e")
            case e : NodeEvent =>
                logger.info(s"node event $e")
                if (isMaster == false) {
                    asClient.children(MASTER).onComplete {
                        case Success(vv) => {
                            logger.info(s"children $vv")
                            checkLeader(value, vv)
                        }
                        case Failure(ee) => {
                            logger.error("failure", ee)
                        }
                    }
                }
        }.children(MASTER)/*.onComplete {
            case Success(v) =>
                logger.info(s"children of $MASTER changed")
                checkLeader(value, v)
            case Failure(e) =>
                logger.error("watch session error", e)
        }*/
    }
}
