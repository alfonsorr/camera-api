package mains

import actors.{CamerasReception, NexusCamerasList}
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import endpoint.NexusEndpoint
import utils.{Statics, SwaggerConfig}
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

object BootBackNode extends App {

  val config = ConfigFactory.parseResources("back.conf").withFallback(ConfigFactory.load())
  private implicit val system:ActorSystem = ActorSystem("default",config)
  private implicit val executor:ExecutionContext = system.dispatcher
  private implicit val materializer:Materializer = ActorMaterializer()

  val swaggerConfig = new SwaggerConfig()
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
