package com.github.sioncheng.fs

import org.apache.zookeeper.Watcher.Event.KeeperState
import org.apache.zookeeper.ZooDefs.Ids

import scala.collection.JavaConverters

object StartApp extends App {

    println("fs app")

    import com.twitter.util.JavaTimer
    import com.twitter.conversions.time._
    import com.twitter.zk._

    implicit val timer = new JavaTimer(true)
    val zk =  ZkClient("127.0.0.1:2181", Some(5.seconds), 30.seconds)


    val pf : PartialFunction[StateEvent, Unit] = {
        case x => println(x)
            println(x.state == KeeperState.SyncConnected)
    }
    zk.onSessionEvent(pf)

    println(zk.name)

    val zNode = zk.apply("/root")
    val acl =  JavaConverters.asScalaBuffer(Ids.OPEN_ACL_UNSAFE)
    zNode.exists.apply().onSuccess(z => println(s"exists ? ${z}"))
    zNode.create(acls = acl).onSuccess(z => println(s"success ${z.name}")).onFailure(z => println(z.getMessage))

    io.StdIn.readLine()
}
