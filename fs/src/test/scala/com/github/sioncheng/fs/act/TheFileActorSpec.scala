package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.sioncheng.cnf.AppConfigurationLoader
import com.github.sioncheng.prtl.{CommandCode, FileCommand}
import com.github.sioncheng.prtl.outer.CreateFile
import com.google.protobuf.ByteString
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class TheFileActorSpec() extends TestKit(ActorSystem("file-actor-spec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    override def afterAll(): Unit = {
        TestKit.shutdownActorSystem(system)
    }

    "file actor " should {
        "send exception message to main actor while create file actor failure" in {

            //val filePath = getClass.getResource("/appconf.json").getPath
            //val appConfiguration = AppConfigurationLoader.loadFromFile(filePath)

            val props = Props.create(classOf[FileActor],
                AppConfigurationLoader.loadFromResourceFile("/appconf.json"),
                self)

            val fileActor = system.actorOf(props)

            val createFileMessageData = CreateFile.CreateFileMessage.newBuilder()
                .setPath("a.txt")
                .setContentLength(10130)
                .setPartitions(3)
                .setPartitionNo(1)
                .setPartitionLength(10)
                .setData(ByteString.copyFrom("abcdefghij".getBytes))
                .build().toByteArray

            val fileCommand = FileCommand(CommandCode.CreateFile, createFileMessageData)

            fileActor ! fileCommand

            Thread.sleep(2000)

            expectMsg(FileOperationException(CommandCode.CreateFile, "a.txt"))
        }
    }
}
