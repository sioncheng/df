import sbtassembly.PathList

name := "df"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val commonSettings = Seq(
    version :="0.1",
    organization := "com.github.sioncheng",
    scalaVersion := "2.12.3",
    libraryDependencies += "org.specs2" % "specs2-core_2.12" % "3.8.6" % "test",
    libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.4.10",
    //libraryDependencies += "com.typesafe.akka" % "akka-actor_2.12" % "2.5.4",
    //libraryDependencies += "com.typesafe.akka" % "akka-stream_2.12" % "2.5.4",
    libraryDependencies += "com.typesafe.akka" % "akka-http_2.12" % "10.0.9",
    libraryDependencies += "com.twitter" % "util-collection_2.12" % "6.45.0",
    libraryDependencies += "com.twitter" % "util-zk-common_2.12" % "6.42.0",
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
        case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
        case PathList("com", "google", xs @ _*) => MergeStrategy.last
        case x =>
            val oldStrategy = (assemblyMergeStrategy in assembly).value
            oldStrategy(x)
    }
)

lazy val common = (project in file("common"))
    .settings(
        commonSettings
        // other settings,
    )

lazy val fm = (project in file("fm")).settings(
    commonSettings,
    mainClass in (Compile, packageBin) := Some("com.github.sioncheng.df.StartApp"),
    mainClass in (Compile, run) := Some("com.github.sioncheng.df.StartApp"),
    mainClass in assembly := Some("com.github.sioncheng.df.StartApp")
).dependsOn(common)


lazy val fs = (project in file("fs")).settings(
    commonSettings,
    mainClass in (Compile, packageBin) := Some("com.github.sioncheng.fs.StartApp"),
    mainClass in (Compile, run) := Some("com.github.sioncheng.fs.StartApp"),
    mainClass in assembly := Some("com.github.sioncheng.fs.StartApp")
).dependsOn(common)