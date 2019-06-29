name := "CalculatorAPI"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.23"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.8"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
