package org.alfiler.actors.paparazzi

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe
import org.alfiler.PhotoOptions
import org.alfiler.actors.paparazzi.Paparazzi.SnapPhoto
import org.alfiler.camera.Camera


object Paparazzi {
  case object SnapPhoto
  def props(photoOptions: PhotoOptions, camera: Camera = Camera.defaultCamera) =
    Props(new Paparazzi(photoOptions, camera))
}

class Paparazzi(photoOptions: PhotoOptions, camera: Camera) extends Actor with ActorLogging{

  import context.dispatcher

  override def receive: Receive = {
    case SnapPhoto => camera.takePicture(photoOptions) pipeTo context.parent
  }
}
