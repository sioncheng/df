
name := "df"


lazy val commonSettings = Seq(
    version :="0.1",
    organization := "com.github.sioncheng",
    scalaVersion := "2.12.3",
    libraryDependencies += "org.specs2" % "specs2-core_2.12" % "3.8.6" % "test",
    libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.4.10"
)

lazy val common = (project in file("common"))
    .settings(
        commonSettings,
        // other settings
    )

lazy val fm = (project in file("fm")).settings(
    commonSettings,
    mainClass in (Compile, packageBin) := Some("com.github.sioncheng.df.StartApp"),
    mainClass in (Compile, run) := Some("com.github.sioncheng.df.StartApp"),
).dependsOn(common)


lazy val fs = (project in file("fs")).settings(
    commonSettings,
    mainClass in (Compile, packageBin) := Some("com.github.sioncheng.fs.StartApp"),
    mainClass in (Compile, run) := Some("com.github.sioncheng.fs.StartApp"),
).dependsOn(common)
