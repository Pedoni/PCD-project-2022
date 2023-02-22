version := "0.1.0-SNAPSHOT"

scalaVersion := "3.1.0"

val akkaVersion = "2.7.0"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion
)

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.5" % Runtime

lazy val root = (project in file("."))
  .settings(
    name := "DistributedPuzzle_Actors"
  )
