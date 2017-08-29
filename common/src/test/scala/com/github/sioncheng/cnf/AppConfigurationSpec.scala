package com.github.sioncheng.cnf

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class AppConfigurationSpec extends WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

    "AppConfiguration " must {
        "parse from string" in {

            val appConfiguration = AppConfiguration("""{"a":1,"b":"d"}""")
            appConfiguration.getInt("a").isEmpty shouldBe false
            appConfiguration.getString("b").getOrElse("") shouldBe "d"
        }

        "load from file" in {
            val filePath = getClass.getResource("/appconf.json").getPath
            val appConfiguration = AppConfigurationLoader.loadFromFile(filePath)
            appConfiguration.getString("listen-on").isEmpty shouldBe(false)
            appConfiguration.getString("export-on").getOrElse("") shouldBe("127.0.0.1:6000")
        }

        "load from resource file" in {
            val appConfiguration = AppConfigurationLoader.loadFromResourceFile("/appconf.json")
            appConfiguration.getString("listen-on").isEmpty shouldBe(false)
            appConfiguration.getString("export-on").getOrElse("") shouldBe("127.0.0.1:6000")
        }
    }
}
