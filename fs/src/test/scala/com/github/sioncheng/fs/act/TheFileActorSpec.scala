package com.github.sioncheng.fs.act

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.sioncheng.cnf.AppConfigurationLoader
import com.github.sioncheng.prtl._
import com.github.sioncheng.prtl.outer._
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

            val findFileAMessageData = FindFile.FindFileMessage.newBuilder()
                .setPath("a.txt")
                .build()
                .toByteArray

            val findFileACommand = FileCommand(2, CommandCode.FindFile, findFileAMessageData)

            fileActor ! FileCommandMessage(findFileACommand, sourceId)

            expectMsgPF(2 second,"") ({
                case x : FileCommandMessage
                    if (x.sourceId.equalsIgnoreCase(sourceId)
                            && x.fc.commandCode == CommandCode.FindFileResult) =>
                    println("find file result success")
            })


            val findFileBMessageData = FindFile.FindFileMessage.newBuilder()
                .setPath("b.txt")
                .build()
                .toByteArray

            val findFileBCommand = FileCommand(3, CommandCode.FindFile, findFileBMessageData)

            fileActor ! FileCommandMessage(findFileBCommand, sourceId)

            expectMsgPF(2 second,"") ({
                case x : FileCommandMessage
                    if (x.sourceId.equalsIgnoreCase(sourceId) && x.fc.commandCode == CommandCode.FindFileResult) =>
                    println(x)
            })
        }

        "create a file and find it and open it" in {

            val sourceId = "cafafiaoi";



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

            val expectDeleteFileResult: (Array[Byte] => Boolean) = (data: Array[Byte]) => {
                val deleteFileResult = DeleteFileResult.DeleteFileResultMessage.parseFrom(data)
                deleteFileResult.getSuccess
            }
            expectMsgPF(2 second,"") ({
                case x: FileCommandMessage
                    if (x.sourceId.equalsIgnoreCase(sourceId)
                            && x.fc.commandCode == CommandCode.DeleteFileResult
                            && expectDeleteFileResult(x.fc.data)) =>
                    println(x)
            })

            val fileData1 = "hello akka".getBytes
            val fileData2 = " hello scala".getBytes

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
            fileActor ! FileCommandMessage(createFileCommand1, sourceId)
            fileActor ! FileCommandMessage(createFileCommand2, sourceId)

            val expectCreateFileResult = (data: Array[Byte]) => {
                val createFileResult = CreateFileResult.CreateFileResultMessage.parseFrom(data)
                createFileResult.getSuccess
            }
            expectMsgPF(2 second,"") ({
                case x: FileCommandMessage
                    if (x.sourceId.equalsIgnoreCase(sourceId)
                            && x.fc.commandCode == CommandCode.CreateFileResult
                            && expectCreateFileResult(x.fc.data)) =>
                    println(x)
            })

            val findFileAMessageData = FindFile.FindFileMessage.newBuilder()
                .setPath("aa.txt")
                .build()
                .toByteArray

            val findFileACommand = FileCommand(7, CommandCode.FindFile, findFileAMessageData)

            fileActor ! FileCommandMessage(findFileACommand, sourceId)

            val expectFindFileResultA = (data: Array[Byte]) => {
                val findFileResult = FindFileResult.FindFileResultMessage.parseFrom(data)
                findFileResult.getSuccess
            }
            expectMsgPF(2 second,"") ({
                case x: FileCommandMessage
                    if (x.sourceId.equalsIgnoreCase(sourceId)
                            && x.fc.commandCode == CommandCode.FindFileResult
                            && expectFindFileResultA(x.fc.data)) =>
                    println(x)
            })

            val openFileMessageData = OpenFile.OpenFileMessage.newBuilder()
                .setPath("aa.txt")
                .build()
                .toByteArray
            val openFileCommand = FileCommand(8, CommandCode.OpenFile, openFileMessageData)

            fileActor ! FileCommandMessage(openFileCommand, sourceId)

            val expectOpenFileResult = (data: Array[Byte]) => {
                val openFileResult = OpenFileResult.OpenFileResultMessage.parseFrom(data)
                openFileResult.getData.toStringUtf8.equalsIgnoreCase("hello akka hello scala")
            }

            expectMsgPF(2 second,"")({
                case x: FileCommandMessage
                    if (x.sourceId.equalsIgnoreCase(sourceId) && expectOpenFileResult(x.fc.data)) =>
                    println(x)
            })
        }

    }
}
