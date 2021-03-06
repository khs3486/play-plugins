import sbt._
import sbt.Keys._

object StatsdBuild extends Build {

  val buildVersion =  "2.2.0"
  val playVersion =  "2.2.0"
  
  val typesafeSnapshot = "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
  val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  val repo = if (buildVersion.endsWith("SNAPSHOT")) typesafeSnapshot else typesafe
  
  lazy val root = Project(id = "play-statsd", base = file("."), settings = Project.defaultSettings).settings(
    version := buildVersion,
    scalaVersion := "2.10.0",
    publishTo <<= (version) { version: String =>
                val nexus = "https://private-repo.typesafe.com/typesafe/"
                if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "maven-snapshots/")
                else                                   Some("releases"  at nexus + "maven-releases/")
    },
    organization := "com.typesafe.play.plugins",
    resolvers += repo,
    libraryDependencies ++= Seq(
        "com.typesafe.play"  %% "play"         % playVersion   % "provided",
        "com.typesafe.play"  %% "play-test"    % playVersion   % "test"
    ),
    parallelExecution in test := false,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v"),
    javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")
  )

  lazy val sample = play.Project(name = "play-statsd-sample", path = file("sample/sample-statsd")).settings(
    Keys.fork in test := false
  ).dependsOn(root).aggregate(root)

}
