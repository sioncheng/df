package com.github.sioncheng.fs.act

import java.io.{PrintWriter, StringWriter}
import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.github.sioncheng.cnf.AppConfiguration
import com.loopfor.zookeeper.{ACL, AsynchronousZookeeper, ChildrenChanged, Configuration, Connected, Disconnected, Ephemeral, EphemeralSequential, Expired, NodeEvent, Persistent, Session, StateEvent, Status, Zookeeper}
import org.apache.zookeeper.KeeperException.NoNodeException

import scala.util.{Failure, Success}

class ZookeeperActor(val mainActor: ActorRef, val appConf: AppConfiguration) extends Actor {

    val ELECTION = "/election"
    val WORKER = "/workers"
    val MASTER = "/master"
    val ROOT = "/fs-root"

    val logger = Logging(context.system, classOf[ZookeeperActor])

    var zk: Zookeeper = null
    var asClient: AsynchronousZookeeper = null
    var session: Session  = null
    val servers = List(new InetSocketAddress("127.0.0.1", 2181))
    val conf: Configuration = Configuration(servers).withWatcher((se,s) => {
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
            case Expired =>
                logger.info("re create session")
                session = null
                zk = Zookeeper(conf)
        }
    }).build()

    zk = Zookeeper(conf)
    println("zk = Zookeeper")
    import scala.concurrent.ExecutionContext.Implicits.global

    var isMaster: Boolean = false

    def initRole(): Unit = {

        val rootData = FileOp.serializeStatus(FileOp.Status(true)).getBytes()
        createIfNotExists(ROOT, (), rootData)
        createIfNotExists(MASTER, ())
        createIfNotExists(WORKER, ())
        createIfNotExists(ELECTION, createMasterFlag())

        def createIfNotExists(path: String, f: => Unit, data: Array[Byte] = null): Unit = {
            asClient.exists(path).onComplete {
                case Success(v) => {
                    logger.info(s"$path exists $v")
                    println(s"$path exists $v")
                    f
                }
                case Failure(e) => {
                    if (e.isInstanceOf[NoNodeException]) {
                        logger.info(s"no $path then create it")
                        println(s"no $path then create it")

                        val dd = if (data == null) "".getBytes() else data
                        asClient.create(path,
                            dd,
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
            val exportOn = appConf.getString("export-on").getOrElse("")
            asClient.create(s"$ELECTION/m_",
                exportOn.getBytes(),
                ACL.AnyoneAll,
                EphemeralSequential).onComplete {
                case Success(value) => {
                    logger.info(s"success $value")
                    asClient.children(ELECTION).onComplete {
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

    def checkLeader(value: String, vv: (Seq[String], Status)): Unit = {
        val num = value.substring(s"$ELECTION/".length)
        val children = vv._1.sorted
        if (children.isEmpty || children.head.equalsIgnoreCase(num)) {
            logger.info("i am the leader")
            isMaster = true
            unregisterWorkers(num)
            registerMaster()
            mainActor ! Leader()

            watchWorkers()
        } else {
            logger.info("oops, i am not the leader.")
            isMaster = false
            registerWorkers(num)
            mainActor ! Worker()
        }

        watchElection(value)
    }

    def registerMaster(): Unit = {
        val exportOn = appConf.getString("export-on").getOrElse("")
        asClient.create(s"$MASTER/m",
            exportOn.getBytes(),
            ACL.AnyoneAll,
            Ephemeral).onComplete {
            case Success(v) =>
                logger.info(s"became master $v")
            case Failure(e) =>
                logger.error("became master failure", e)
        }
    }


    def registerWorkers(name: String): Unit = {
        val exportOn = appConf.getString("export-on").getOrElse("")
        val path = s"$WORKER/$name"
        asClient.exists(path).onComplete {
            case Success(v) =>
                logger.info(s"exists $path $v")
            case Failure(e) =>
                logger.error(s"doesn't exists $path", e)

                asClient.create(s"$WORKER/$name",
                    exportOn.getBytes(),
                    ACL.AnyoneAll,
                    Ephemeral).onComplete {
                    case Success(v) =>
                        logger.info(s"became worker $name $v")
                    case Failure(e) =>
                        logger.error(s"became worker $name failure", e)
                        e.printStackTrace()
                }
        }
    }

    def unregisterWorkers(name: String): Unit = {
        var exists: Boolean = false
        val path = s"$WORKER/$name"
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
                    val sw = new StringWriter
                    val pw = new PrintWriter(sw)
                    e.printStackTrace(pw)
                    logger.error(s"un-became worker $name failure ${sw.toString}", e)
            }
        }
    }

    def watchElection(value: String): Unit = {
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
                    asClient.children(ELECTION).onComplete {
                        case Success(vv) => {
                            logger.info(s"children $vv")
                            checkLeader(value, vv)
                        }
                        case Failure(ee) => {
                            logger.error("failure", ee)
                        }
                    }
                }
        }.children(ELECTION)/*.onComplete {
            case Success(v) =>
                logger.info(s"children of $MASTER changed")
                checkLeader(value, v)
            case Failure(e) =>
                logger.error("watch session error", e)
        }*/
    }

    def watchWorkers(): Unit = {
        asClient.watch {
            case e: ChildrenChanged =>
                logger.info(s"workers changed $e")
                asClient.children(WORKER).onComplete {
                    case Success(children) =>
                        logger.info(s"workers $children")
                        mainActor ! RegisterWorkers(children._1)
                        children._1.foreach(name => {
                            asClient.get(s"$WORKER/$name").onComplete {
                                case Success(value) =>
                                    logger.info(s"get $WORKER/$name $value")
                                    mainActor ! RegisterWorker(name, value._1)
                                case Failure(f) =>
                                    logger.info(s"get $WORKER/$name failure", f)
                            }
                        })

                        watchWorkers()
                    case Failure(f) =>
                        logger.error(s"get workers failure $f")
                }
            case x =>
                logger.info(s"something happened during watch workers $x")
        }.children(WORKER)
    }




    override def receive: Receive = {
        case ls : FileOp.List =>
            logger.info(s"list ${ls.path}")
            list(ls.path, sender())
        case x => logger.info(s"receive $x")
    }


    def list(path: String, asker: ActorRef): Unit = {
        asClient.get(path).onComplete {
            case Success(v) =>
                logger.info(s"get path $path $v")
                val s = FileOp.deserializeStatus(new String(v._1))
                if (s.isDir) {
                    asClient.children(path).onComplete {
                        case Success(vv) =>
                            asker ! FileOp.Directory(path, vv._1)
                        case Failure(ee) =>
                            logger.error(s"children on $path error", ee)
                    }
                } else {
                    asker ! FileOp.File(path, "".getBytes())
                }
            case Failure(e) =>
                logger.error(s"get path $path error", e)

        }
    }

    override def postStop(): Unit = {
        if (asClient != null) {
            asClient.close()
        }

        if (zk != null) {
            zk.close()
        }
    }

}
