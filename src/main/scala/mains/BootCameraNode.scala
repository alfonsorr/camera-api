package mains

import java.util.Calendar
import java.util.concurrent.TimeUnit

import actors.CameraClusterAware
import actors.paparazzi.{Paparazzi, SnapPhoto}
import actors.photoGetter.PhotoCache
import akka.actor.ActorSystem
import akka.dispatch.Dispatcher
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import camera.{PhotoFormats, PhotoOptions}
import com.google.common.io.ByteStreams
import com.typesafe.config.{Config, ConfigFactory}
import endpoint.PhotoEndpoint
import messages.Photo
import utils.SwaggerConfig

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * Created by Alfonso on 01/02/2016.
  */
object BootCameraNode extends App {

  val confFile = args.map(_+".conf").headOption.getOrElse("camera1.conf")
  val config = ConfigFactory.parseResources(confFile).withFallback(ConfigFactory.load())
  val cameraConfig = config.getConfig("camera")
  val port = cameraConfig.getInt("port")
  private implicit val system:ActorSystem = ActorSystem("default", config)
  private implicit val executor:ExecutionContext = system.dispatcher
  private implicit val materializer:ActorMaterializer = ActorMaterializer()

  val swaggerConfig = new SwaggerConfig()
  val logger = Logging(system, getClass)

  val cache = startActorsAndReturnCache(system,cameraConfig)

  val route = PhotoEndpoint(cache).route ~ swaggerConfig.routes

  Http().bindAndHandle(route, interface = "0.0.0.0", port)

  def startActorsAndReturnCache(system: ActorSystem, cameraConfig:Config) = {
    val periodicCameraConfig = cameraConfig.getConfig("periodicPhotos")
    val nodeName = cameraConfig.getString("name")
    val period = Try{periodicCameraConfig.getDuration("period")}
      .map(d => Duration(d.toNanos, TimeUnit.NANOSECONDS))
      .getOrElse(Duration(5, TimeUnit.SECONDS))
    val takePeriodicPhotos = Try{periodicCameraConfig.getBoolean("active")}.getOrElse(true)
    val nPhotos = 10
    val cache = system.actorOf(PhotoCache.props(nPhotos), "photoCache")
    val paparazzi =
      system.actorOf(Paparazzi.props(PhotoOptions(""), cache), "paparazzi")
    if (takePeriodicPhotos) {
      system.scheduler.schedule(period,
        period,
        paparazzi,
        SnapPhoto)
    } else {
      val photo = ByteStreams.toByteArray(this.getClass.getClassLoader.getResourceAsStream(s"$nodeName.jpg"))
      cache ! Photo(Calendar.getInstance(), photo, PhotoFormats.JPG)
    }
    system.actorOf(CameraClusterAware.props(cache, nodeName))
    cache
  }
}
