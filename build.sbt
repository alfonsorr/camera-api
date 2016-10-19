name := "camera_node"

version := "1.0"

scalaVersion := "2.11.8"

sbtVersion := "0.13.12"

mainClass in assembly := Some("mains.Boot")

libraryDependencies ++= {
  val akkaV = "2.4.10"
  val scalaTestV = "3.0.0"
  val swaggerV = "0.7.2"
  val loggingV = "3.4.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.github.swagger-akka-http" %% "swagger-akka-http" % swaggerV,
    "com.typesafe.scala-logging" %% "scala-logging" % loggingV,
    "org.slf4j" % "slf4j-simple" % "1.7.21"
  )
}