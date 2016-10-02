package mains

import java.util.concurrent.TimeUnit

import actors.CameraClusterAware
import actors.paparazzi.{Paparazzi, SnapPhoto}
import actors.photoGetter.PhotoCache
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import camera.PhotoOptions
import com.typesafe.config.{Config, ConfigFactory}
import endpoint.PhotoEndpoint
import utils.SwaggerConfig

import scala.concurrent.duration.Duration

/**
  * Created by Alfonso on 01/02/2016.
  */
object BootCameraNode extends App {

  val config = ConfigFactory.parseResources("camera1.conf").withFallback(ConfigFactory.load())
  implicit val system = ActorSystem("default", config)
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val swaggerConfig = new SwaggerConfig(system)
  val logger = Logging(system, getClass)

  val takePeriodicPhotos = args.headOption.forall(_.toBoolean)

  val cache = startActorsAndReturnCache(system,takePeriodicPhotos)

  val route = PhotoEndpoint(cache).route ~ swaggerConfig.routes

  Http().bindAndHandle(route, interface = "0.0.0.0", port = 9091)

  def startActorsAndReturnCache(system: ActorSystem, takePeriodicPhotos:Boolean = true) = {
    val nPhotos = 10
    val cache = system.actorOf(PhotoCache.props(nPhotos), "photoCache")
    val paparazzi =
      system.actorOf(Paparazzi.props(PhotoOptions(""), cache), "paparazzi")
    if (takePeriodicPhotos) {
      system.scheduler.schedule(Duration(5, TimeUnit.SECONDS),
        Duration(10, TimeUnit.SECONDS),
        paparazzi,
        SnapPhoto)
    }
    system.actorOf(CameraClusterAware.props(cache))
    cache
  }
}
