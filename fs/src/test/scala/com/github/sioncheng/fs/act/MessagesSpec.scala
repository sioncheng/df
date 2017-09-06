package com.github.sioncheng.fs.act

import org.scalatest.{Matchers, WordSpecLike}

class MessagesSpec extends WordSpecLike with Matchers {

    "FileOp.Status" must {
        "serialize to json" in {
            val s = FileOp.Status(true)
            val js = FileOp.serializeStatus(s)
            js shouldBe("""{"isDir":true}""")
        }

        """deserialize {"isDir":true} to status""" in {
            val s = FileOp.deserializeStatus("""{"isDir":true}""")
            s.isDir shouldBe(true)
        }
    }
}
