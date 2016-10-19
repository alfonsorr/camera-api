package mains

import actors.{CamerasReception, NexusCamerasList}
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import endpoint.NexusEndpoint
import utils.{Statics, SwaggerConfig}
import akka.http.scaladsl.server.Directives._


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

  val camerasList = startActors(system)

  val route = NexusEndpoint(camerasList).route ~ swaggerConfig.routes

  Http().bindAndHandle(route, interface = "0.0.0.0", port = 9090)

  def startActors(system: ActorSystem):ActorRef = {
    val cameraList = system.actorOf(NexusCamerasList.props(), "cameraList")
    system.actorOf(CamerasReception.props(cameraList), Statics.CAMERA_RECEPTION_NAME)
    cameraList
  }
}
