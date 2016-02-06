package utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import scala.reflect.runtime.universe._
import endpoint.Test


/**
  * Created by Alfonso on 06/02/2016.
  */
class SwaggerConfig(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  implicit val actorSystem: ActorSystem = system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes: Seq[scala.reflect.runtime.universe.Type] = Seq(typeOf[Test])
  //override here to change the SwaggerHttpService methods config
}