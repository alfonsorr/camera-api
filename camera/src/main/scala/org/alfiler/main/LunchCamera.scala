package org.alfiler.main

import akka.actor.{ActorRef, ActorSystem}
import org.alfiler.PhotoOptions
import org.alfiler.actors.deliverer.Deliverer
import org.alfiler.actors.deliverer.Deliverer.SendTo
import org.alfiler.actors.paparazzi.Paparazzi

object LunchCamera {
  type Deliverer = ActorRef
  def initializeCameraFlow(implicit system:ActorSystem):Deliverer = {
    val deliverer = system.actorOf(Deliverer.props(), "deliverer")
    val camera = system.actorOf(Paparazzi.props(PhotoOptions()), "camera")
    deliverer ! SendTo("cameras")
    deliverer
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("cameraNode")
    val deliverer = initializeCameraFlow
  }
}
