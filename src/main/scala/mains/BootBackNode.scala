package mains

import actors.CamerasReception
import actors.paparazzi.Paparazzi
import actors.photoGetter.PhotoCache
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import camera.PhotoOptions
import com.typesafe.config.ConfigFactory
import endpoint.PhotoEndpoint
import utils.SwaggerConfig


/**
  * Created by Alfonso on 01/02/2016.
  */
object BootBackNode extends App {

  val config = ConfigFactory.parseResources("back.conf").withFallback(ConfigFactory.load())
  implicit val system = ActorSystem("default",config)
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val swaggerConfig = new SwaggerConfig(system)
  val logger = Logging(system, getClass)

  val cache = startActors(system)

  val route = swaggerConfig.routes

  Http().bindAndHandle(route, interface = "0.0.0.0", port = 9090)

  def startActors(system: ActorSystem) = {
    system.actorOf(CamerasReception.props(), "creception")
  }
}
