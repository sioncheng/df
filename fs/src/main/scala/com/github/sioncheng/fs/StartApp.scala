package com.github.sioncheng.fs

import com.twitter.zk.Connector.EventHandler
import org.apache.zookeeper.Watcher.Event.KeeperState

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
    zk.onSessionEvent (pf)

    println(zk.name)

    zk.apply()
    zk.wait(10000)


    io.StdIn.readLine()
}
