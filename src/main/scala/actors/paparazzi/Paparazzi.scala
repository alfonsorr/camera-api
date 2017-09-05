package actors.paparazzi

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe
import camera.{Camera, PhotoOptions}

case object SnapPhoto

object Paparazzi {
  def props(photoOptions: PhotoOptions, photoDestinyActor:ActorRef) = Props(new Paparazzi(photoOptions, photoDestinyActor))
}

class Paparazzi(photoOptions: PhotoOptions, photoDestinyActor:ActorRef) extends Actor with ActorLogging{

  import context.dispatcher

  override def receive: Receive = {
    case SnapPhoto => Camera.takePicture(photoOptions) pipeTo photoDestinyActor
  }
}
