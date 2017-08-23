package com.github.sioncheng.fs

import java.net.{InetAddress, InetSocketAddress}
import java.nio.charset.Charset

import com.loopfor.zookeeper.{ACL, AsynchronousZookeeper, Configuration, Ephemeral, SynchronousZookeeper, Zookeeper}
import org.apache.zookeeper.ZooDefs.Ids

import scala.collection.JavaConverters


object StartApp extends App {

    println("fs app")

    val servers = List(new InetSocketAddress("127.0.0.1", 2181))
    val conf = Configuration(servers)

    val zk = Zookeeper(conf)

    val client = zk.sync

    if(!client.exists("/root").isEmpty) {
        println(client.create("/root/a",
            "".getBytes(),
            ACL.AnyoneAll,
            Ephemeral))
    }

    io.StdIn.readLine()
}
