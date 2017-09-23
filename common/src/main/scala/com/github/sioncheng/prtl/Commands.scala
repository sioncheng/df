package com.github.sioncheng.prtl

object CommandCode {
    val CreateFile = 1
}

case class Command(commandCode: Int, data:Array[Byte])

object CommandSerializer {
    import java.nio.ByteBuffer

    def toBytes(command: Command): Array[Byte] = {
        val commandCodeBuf = ByteBuffer.allocate(4)
        commandCodeBuf.asIntBuffer().put(command.commandCode)
        val lengthBuf = ByteBuffer.allocate(4)
        lengthBuf.asIntBuffer().put(command.data.length)
        commandCodeBuf.array() ++ lengthBuf.array() ++ command.data
    }

    def parseFrom(data: Array[Byte]): Option[Command] = {
        if (data.length <= 8) {
            None
        } else {
            val commandCodeBuf = ByteBuffer.wrap(data,0, 4)
            val commandCode = commandCodeBuf.asIntBuffer().get(0)
            val lengthBuf = ByteBuffer.wrap(data, 4, 4)
            val length = lengthBuf.asIntBuffer().get(0)
            if (data.length != (4 + 4 + length)) {
                None
            } else {
                Some(Command(commandCode, ByteBuffer.wrap(data, 8, length).array()))
            }
        }

    }
}