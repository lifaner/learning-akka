name := """akkademy-db"""
organization := "com.akkademy"
version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-remote" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

mappings in (Compile, packageBin) ~= { _.filterNot {
  case (_, name) => Seq("application.conf").contains(name)
}}
