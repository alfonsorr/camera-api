package org.alfiler.actors.paparazzi

import java.nio.file.Files
import java.util.Calendar

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.server.directives.FileAndResourceDirectives.ResourceFile
import akka.pattern.pipe
import com.typesafe.config.Config
import org.alfiler.{Photo, PhotoFormats, PhotoOptions}
import org.alfiler.actors.paparazzi.Paparazzi.SnapPhoto
import org.alfiler.camera.Camera

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Try}


object Paparazzi {
  case object SnapPhoto
  def props(photoOptions: PhotoOptions, camera: Camera = Camera.defaultCamera) =
    Props(new Paparazzi(photoOptions, camera))
}

class Paparazzi(photoOptions: PhotoOptions, camera: Camera) extends Actor with ActorLogging{

  import context.dispatcher

  val usableCamera: Camera = {
    Try {
      val cameraConf = context.system.settings.config.getConfig("akka.camera")
      cameraConf.getBoolean("disable")
    }.flatMap(a => Try{
      if (a){
        log.warning("camera disabled")
        new Camera() {
          val fakePhoto:Array[Byte] = Array.empty[Byte]
          override def takePicture(photoOptions: PhotoOptions)(implicit executor: ExecutionContext): Future[Photo] = {
            Future.successful(Photo(Calendar.getInstance(),fakePhoto,PhotoFormats.PNG))
          }
        }
      } else {
        camera
      }
    }).getOrElse(camera)
  }



  override def receive: Receive = {
    case SnapPhoto => usableCamera.takePicture(photoOptions) pipeTo context.parent
  }
}
