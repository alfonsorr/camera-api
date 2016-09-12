package actor.paparazzi

import java.util.{Calendar, Date}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import camera.{Camera, PhotoOptions}

import scala.concurrent.duration.Duration
import akka.pattern.pipe
import messages.Photo

private case class SnapPhoto()

object Paparazzi {
  def props(photoOptions: PhotoOptions, sendTo:ActorRef) = Props(new Paparazzi(photoOptions, sendTo))
}

class Paparazzi(photoOptions: PhotoOptions, sendTo:ActorRef) extends Actor with ActorLogging{

  import context.dispatcher

  context.system.scheduler.schedule(Duration(5, TimeUnit.SECONDS),Duration(10, TimeUnit.SECONDS), self,SnapPhoto())
  override def receive: Receive = {
    case _:SnapPhoto => Camera.takePicture(photoOptions) map (data => Photo(Calendar.getInstance(), data)) pipeTo sendTo
  }
}
