name := """akkademaid"""

version := "1.0"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.syncthemall" % "boilerpipe" % "1.2.2",
  "com.akkademy" %% "akkademy-db" % "0.1-SNAPSHOT",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
