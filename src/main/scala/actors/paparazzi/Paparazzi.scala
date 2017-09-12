package actors.paparazzi

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe
import camera.{Camera, PhotoOptions}

case object SnapPhoto

object Paparazzi {
  def props(photoOptions: PhotoOptions, photoDestinyActor:ActorRef, camera: Camera = Camera.defaultCamera) =
    Props(new Paparazzi(photoOptions, photoDestinyActor, camera))
}

class Paparazzi(photoOptions: PhotoOptions, photoDestinyActor:ActorRef, camera: Camera) extends Actor with ActorLogging{

  import context.dispatcher

  override def receive: Receive = {
    case SnapPhoto => camera.takePicture(photoOptions) pipeTo photoDestinyActor
  }
}
