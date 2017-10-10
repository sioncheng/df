package com.github.sioncheng

import com.github.sioncheng.prtl.{FileCommand, CommandCode, CommandSerializer}
import org.scalatest.{Matchers, WordSpecLike}

class CommandsSpec extends WordSpecLike with Matchers {

    "command serializer " must {
        "should be able to convert command to bytes and parse command from bytes" in {
            val dummyData = "123456".getBytes()
            val command = FileCommand(1, CommandCode.CreateFile, dummyData)
            val bytes = CommandSerializer.toBytes(command)
            bytes.length shouldEqual(4 + 4 + 4 + 6)

            val parsedCommand = CommandSerializer.parseFrom(bytes)
            parsedCommand.isEmpty shouldEqual false
            parsedCommand.head._1.id shouldEqual 1
            parsedCommand.head._1.commandCode shouldEqual CommandCode.CreateFile
        }
    }
}
