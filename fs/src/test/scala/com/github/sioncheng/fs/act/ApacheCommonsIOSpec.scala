package com.github.sioncheng.fs.act

import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.scalatest.{Matchers, WordSpecLike}

class ApacheCommonsIOSpec extends WordSpecLike with Matchers {


    "commons io " must {
        "be able to parse ~ relative user path" in {
            val userPath = FilenameUtils.getFullPath("~/fs/a.txt")
            println(userPath)
            println(FileUtils.getUserDirectoryPath)
        }
    }

}
