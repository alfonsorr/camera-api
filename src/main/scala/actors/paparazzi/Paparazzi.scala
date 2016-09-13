package actors.paparazzi

import java.util.{Calendar, Date}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import camera.{Camera, PhotoOptions}

import scala.concurrent.duration.Duration
import akka.pattern.pipe

private case class SnapPhoto()

object Paparazzi {
  def props(photoOptions: PhotoOptions, photoDestinyActor:ActorRef) = Props(new Paparazzi(photoOptions, photoDestinyActor))
}

class Paparazzi(photoOptions: PhotoOptions, photoDestinyActor:ActorRef) extends Actor with ActorLogging{

  import context.dispatcher

  context.system.scheduler.schedule(Duration(5, TimeUnit.SECONDS),Duration(10, TimeUnit.SECONDS), self,SnapPhoto())
  override def receive: Receive = {
    case _:SnapPhoto => Camera.takePicture(photoOptions) pipeTo photoDestinyActor
  }
}
