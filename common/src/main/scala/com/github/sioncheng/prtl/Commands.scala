package com.github.sioncheng.prtl

object CommandCode {
    val CreateFile = 1
    val DeleteFile = 2
    val OpenFile = 3
    val FindFile = 4

    val CreateFileResult = 11
    val DeleteFileResult = 12
    val OpenFileResult = 13
    val FindFileResult = 14
}

case class FileCommand(id: Int, commandCode: Int, data:Array[Byte])
case class FileCommandMessage(fc: FileCommand, sourceId: String)


case class FileOperationException(code: Int, path: String, message: String, sourceId: String)
case class FinishedFileOperation(code: Int, path: String, sourceId: String)

object FileCommandSerializer {
    import java.nio.ByteBuffer

    def toBytes(command: FileCommand): Array[Byte] = {
        val idBuf = ByteBuffer.allocate(4)
        idBuf.asIntBuffer().put(command.id)
        val commandCodeBuf = ByteBuffer.allocate(4)
        commandCodeBuf.asIntBuffer().put(command.commandCode)
        val lengthBuf = ByteBuffer.allocate(4)
        lengthBuf.asIntBuffer().put(command.data.length)
        idBuf.array() ++ commandCodeBuf.array() ++ lengthBuf.array() ++ command.data
    }

    def parseFrom(data: Array[Byte]): Option[(FileCommand, Int)] = {
        parseFrom(data, 0, data.length)
    }

    def parseFrom(data: Array[Byte], index: Int, size: Int): Option[(FileCommand, Int)] = {
        if (size <= 12) {
            None
        } else {
            val idBuf = ByteBuffer.wrap(data, index, 4)
            val id = idBuf.asIntBuffer().get(0)
            val commandCodeBuf = ByteBuffer.wrap(data,4 + index, 4)
            val commandCode = commandCodeBuf.asIntBuffer().get(0)
            if (false == isValidCommandCode(commandCode)) {
                None
            } else {
                val lengthBuf = ByteBuffer.wrap(data, 8 + index, 4)
                val dataLength = lengthBuf.asIntBuffer().get(0)
                val totalLength = 12 + index + dataLength
                if (size < totalLength) {
                    None
                } else {
                    Some((FileCommand(id, commandCode, ByteBuffer.wrap(data, 12 + index, dataLength).array())), totalLength)
                }
            }
        }
    }

    def isValidCommandCode(code: Int): Boolean = {
        code == CommandCode.CreateFile
    }
}
