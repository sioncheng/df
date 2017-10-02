package com.github.sioncheng.prtl

object CommandCode {
    val CreateFile = 1
}

case class FileCommand(commandCode: Int, data:Array[Byte])

object CommandSerializer {
    import java.nio.ByteBuffer

    def toBytes(command: FileCommand): Array[Byte] = {
        val commandCodeBuf = ByteBuffer.allocate(4)
        commandCodeBuf.asIntBuffer().put(command.commandCode)
        val lengthBuf = ByteBuffer.allocate(4)
        lengthBuf.asIntBuffer().put(command.data.length)
        commandCodeBuf.array() ++ lengthBuf.array() ++ command.data
    }

    def parseFrom(data: Array[Byte]): Option[(FileCommand, Int)] = {
        parseFrom(data, 0, data.length)
    }

    def parseFrom(data: Array[Byte], index: Int, size: Int): Option[(FileCommand, Int)] = {
        if (size <= 8) {
            None
        } else {
            val commandCodeBuf = ByteBuffer.wrap(data,index, 4)
            val commandCode = commandCodeBuf.asIntBuffer().get(0)
            if (false == isValidCommandCode(commandCode)) {
                None
            } else {
                val lengthBuf = ByteBuffer.wrap(data, 4 + index, 4)
                val dataLength = lengthBuf.asIntBuffer().get(0)
                val totalLength = 8 + index + dataLength
                if (size < totalLength) {
                    None
                } else {
                    Some((FileCommand(commandCode, ByteBuffer.wrap(data, 8 + index, dataLength).array())), totalLength)
                }
            }
        }
    }

    def isValidCommandCode(code: Int): Boolean = {
        code == CommandCode.CreateFile
    }
}