import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import endpoint.Test
import utils.SwaggerConfig


/**
  * Created by Alfonso on 01/02/2016.
  */
object Boot extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val swaggerConfig = new SwaggerConfig(system)
  val logger = Logging(system, getClass)

  val route = Test().route ~ swaggerConfig.routes

  Http().bindAndHandle(route, interface = "0.0.0.0", port = 9090)
}
