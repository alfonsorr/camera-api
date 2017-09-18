name := "camera_node"

version := "1.0"

scalaVersion := "2.12.3"


lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scalaVersion.value
)

lazy val coreDependencies = {
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

lazy val californiumDependencies = {
  val californiumVersion = "1.0.6"
  Seq(
    "org.eclipse.californium" % "californium-core" % californiumVersion,
    "org.eclipse.californium" % "scandium" % californiumVersion,
    "org.eclipse.californium" % "element-connector" % californiumVersion
  )
}


libraryDependencies ++= coreDependencies


lazy val camera = project.in(file("camera"))
  .settings(commonSettings)
  .settings(libraryDependencies ++= coreDependencies)
  .dependsOn(cameraPublic)

lazy val cameraPublic = project.in(file("camera-public"))
  .settings(commonSettings)


lazy val light = project.in(file("light"))
  .settings(commonSettings)
  .settings(libraryDependencies ++= coreDependencies ++ californiumDependencies)

lazy val toSplit = project.in(file("to-split"))
  .settings(commonSettings)
  .settings(libraryDependencies ++= coreDependencies)
    .dependsOn(camera)

lazy val allModules = project.in(file("."))
  .settings(commonSettings)
  .settings(libraryDependencies ++= coreDependencies)
.aggregate(camera, cameraPublic, light, toSplit)