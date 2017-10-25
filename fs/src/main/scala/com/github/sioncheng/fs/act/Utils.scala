package com.github.sioncheng.fs.act

import org.apache.commons.io.FileUtils

object Utils {

    def parseRoot(root: String): String = {
        if (root.startsWith("~")) {
            val userPath = FileUtils.getUserDirectory
            userPath + root.substring(1)
        } else {
            root
        }
    }
}
