name := "akkaHttp"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV = "2.4.1"
  val akkaStreamV = "2.0.3"
  val scalaTestV = "2.2.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.6.2",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  )
}