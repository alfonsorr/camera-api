package org.alfiler.main

import akka.actor.{ActorRef, ActorSystem}
import org.alfiler.actors.config.PhotoConfigurator
import org.alfiler.actors.config.PhotoConfigurator.CameraFlowConfig

object LunchCamera {
  type Configurator = ActorRef
  def initializeCameraFlow(implicit system:ActorSystem):Configurator = {
    val configurator = system.actorOf(PhotoConfigurator.props())
    configurator ! CameraFlowConfig() //send default config for now
    configurator
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("cameraNode")
    val configurator = initializeCameraFlow
  }
}
