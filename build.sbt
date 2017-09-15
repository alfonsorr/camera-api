name := "camera_node"

version := "1.0"

scalaVersion := "2.12.3"

sbtVersion := "0.13.12"

mainClass in assembly := Some("mains.Boot")

libraryDependencies ++= {
  val akkaV = "2.5.4"
  val akkaHTTPV = "10.0.9"
  val scalaTestV = "3.0.0"
  val swaggerV = "0.11.0"
  val loggingV = "3.7.2"
  val slf4jV = "1.7.25"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHTTPV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaV,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaV % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHTTPV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHTTPV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.github.swagger-akka-http" %% "swagger-akka-http" % swaggerV,
    "com.typesafe.scala-logging" %% "scala-logging" % loggingV,
    "org.slf4j" % "slf4j-simple" % slf4jV
  )
}