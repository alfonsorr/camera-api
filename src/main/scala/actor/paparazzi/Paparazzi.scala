package actor.paparazzi

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import camera.{Camera, PhotoOptions}

import scala.concurrent.duration.Duration
import akka.pattern.pipe

case class SnapPhoto()

class Paparazzi(photoOptions: PhotoOptions, sendTo:ActorRef) extends Actor{

  import context.dispatcher

  context.system.scheduler.schedule(Duration(20, TimeUnit.SECONDS),Duration(20, TimeUnit.SECONDS), self,SnapPhoto())
  override def receive: Receive = {
    case _:SnapPhoto => Camera.takePicture(photoOptions) pipeTo sendTo
  }
}
