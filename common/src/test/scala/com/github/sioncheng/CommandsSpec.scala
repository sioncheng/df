package com.github.sioncheng

import com.github.sioncheng.prtl.{Command, CommandCode, CommandSerializer}
import org.scalatest.{Matchers, WordSpecLike}

class CommandsSpec extends WordSpecLike with Matchers {

    "command serializer " must {
        "should be able to convert command to bytes and parse command from bytes" in {
            val dummyData = "123456".getBytes()
            val command = Command(CommandCode.CreateFile, dummyData)
            val bytes = CommandSerializer.toBytes(command)
            bytes.length shouldEqual(4+4+6)

            val parsedCommand = CommandSerializer.parseFrom(bytes)
            parsedCommand.isEmpty shouldEqual(false)
            parsedCommand.head.commandCode shouldEqual(CommandCode.CreateFile)
        }
    }
}
