package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.sioncheng.cnf.AppConfigurationLoader
import com.github.sioncheng.prtl._
import com.github.sioncheng.prtl.outer.{CreateFile, DeleteFile, FilePath, OpenFile}
import com.google.protobuf.ByteString
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

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

            val sourceId = "semtawcfaf"

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

            val fileCommand = FileCommand(1, CommandCode.CreateFile, createFileMessageData)

            fileActor ! FileCommandMessage(fileCommand, sourceId)

            Thread.sleep(1000)

            expectMsg(FileOperationException(CommandCode.CreateFile, "a.txt", "expect 0 but 1", sourceId))
        }

        "be able to tell if file exists" in {
            val sourceId = "battife"

            val appConfig = AppConfigurationLoader.loadFromResourceFile("/appconf.json")
            val props = Props.create(classOf[FileActor],
                appConfig,
                self)

            val fileActor = system.actorOf(props)

            val findFileAMessageData = FilePath.FilePathMessage.newBuilder()
                .setPath("a.txt")
                .build()
                .toByteArray

            val findFileACommand = FileCommand(2, CommandCode.FindFile, findFileAMessageData)

            fileActor ! FileCommandMessage(findFileACommand, sourceId)

            val root = Utils.parseRoot(appConfig.getString("fs-root").get)

            val findFileAResult = FileCommandResult(CommandCode.FindFile, root, "a.txt", true, sourceId, None)

            expectMsg(findFileAResult)


            val findFileBMessageData = FilePath.FilePathMessage.newBuilder()
                .setPath("b.txt")
                .build()
                .toByteArray

            val findFileBCommand = FileCommand(3, CommandCode.FindFile, findFileBMessageData)

            fileActor ! FileCommandMessage(findFileBCommand, sourceId)

            val findFileBResult = FileCommandResult(CommandCode.FindFile, root, "b.txt", false, sourceId, None)

            Thread.sleep(1000)

            expectMsg(findFileBResult)
        }

        "create a file and find it and open it" in {

            val sourceId = "cafafiaoi";

            val fileData1 = "hello akka".getBytes
            val fileData2 = "hello scala".getBytes

            val createFileMessageData1 = CreateFile.CreateFileMessage.newBuilder()
                .setPath("aa.txt")
                .setContentLength(fileData1.length + fileData2.length)
                .setPartitions(2)
                .setPartitionNo(0)
                .setPartitionLength(fileData1.length)
                .setData(ByteString.copyFrom(fileData1))
                .build()
                .toByteArray

            val createFileCommand1 = FileCommand(4, CommandCode.CreateFile, createFileMessageData1)

            val createFileMessageData2 = CreateFile.CreateFileMessage.newBuilder()
                .setPath("aa.txt")
                .setContentLength(fileData1.length + fileData2.length)
                .setPartitions(2)
                .setPartitionNo(1)
                .setPartitionLength(fileData2.length)
                .setData(ByteString.copyFrom(fileData2))
                .build()
                .toByteArray

            val createFileCommand2 = FileCommand(5, CommandCode.CreateFile, createFileMessageData2)

            val appConfig = AppConfigurationLoader.loadFromResourceFile("/appconf.json")
            val props = Props.create(classOf[FileActor],
                appConfig,
                self)

            val fileActor = system.actorOf(props)

            val deleteFileMessageData = DeleteFile.DeleteFileMessage.newBuilder()
                .setPath("aa.txt")
                .build()
                .toByteArray
            val deleteFileCommand = FileCommand(6, CommandCode.DeleteFile, deleteFileMessageData)
            fileActor ! FileCommandMessage(deleteFileCommand, sourceId)

            val root = Utils.parseRoot(appConfig.getString("fs-root").get)

            val deleteFileResult = FileCommandResult(CommandCode.DeleteFile, root, "aa.txt", true, sourceId, None)
            Thread.sleep(1000)
            expectMsg(deleteFileResult)

            fileActor ! FileCommandMessage(createFileCommand1, sourceId)
            fileActor ! FileCommandMessage(createFileCommand2, sourceId)

            val createFileResult = FileCommandResult(CommandCode.CreateFile, root, "aa.txt", true, sourceId, None)

            Thread.sleep(1000)

            expectMsg(createFileResult)

            val findFileAMessageData = FilePath.FilePathMessage.newBuilder()
                .setPath("aa.txt")
                .build()
                .toByteArray

            val findFileACommand = FileCommand(7, CommandCode.FindFile, findFileAMessageData)

            fileActor ! FileCommandMessage(findFileACommand, sourceId)

            val findFileAResult = FileCommandResult(CommandCode.FindFile, root, "aa.txt", true, sourceId, None)

            Thread.sleep(1000)

            expectMsg(findFileAResult)

            val openFileMessageData = FilePath.FilePathMessage.newBuilder()
                .setPath("aa.txt")
                .build()
                .toByteArray
            val openFileCommand = FileCommand(8, CommandCode.OpenFile, openFileMessageData)

            fileActor ! FileCommandMessage(openFileCommand, sourceId)

            expectMsgPF(2 second,"")({
                case x: FileCommandResult if (x.sourceId.equalsIgnoreCase(sourceId)) =>
                    println(x)
            })
        }

    }
}
