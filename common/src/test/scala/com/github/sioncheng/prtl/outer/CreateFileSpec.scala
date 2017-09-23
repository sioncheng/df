package com.github.sioncheng.prtl.outer

import com.github.sioncheng.prtl.outer.CreateFile.CreateFileMessage
import com.google.protobuf.ByteString
import org.scalatest.{Matchers, WordSpecLike}

class CreateFileSpec extends WordSpecLike with Matchers {

    "CreateFile proto buf" must {
        "be able to serialize " in {

            val data = "abcdefghiklm"

            val createFileMessageBuilder = CreateFileMessage.newBuilder()
            createFileMessageBuilder.setPath("/f/a.txt")
            createFileMessageBuilder.setContentLength(12)
            createFileMessageBuilder.setPartitions(1)
            createFileMessageBuilder.setPartitionNo(0)
            createFileMessageBuilder.setPartitionLength(12)
            createFileMessageBuilder.setData(ByteString.copyFrom(data.getBytes()))

            val createFileMessage = createFileMessageBuilder.build();

            val bytesArray = createFileMessage.toByteArray

            val createFileMessage2 = CreateFileMessage.parseFrom(bytesArray)

            createFileMessage2.getPath shouldEqual("/f/a.txt")
        }
    }
}
