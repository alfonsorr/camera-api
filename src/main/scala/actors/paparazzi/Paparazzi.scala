package actors.paparazzi

import java.util.{Calendar, Date}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import camera.{Camera, PhotoOptions}

import scala.concurrent.duration.Duration
import akka.pattern.pipe

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
