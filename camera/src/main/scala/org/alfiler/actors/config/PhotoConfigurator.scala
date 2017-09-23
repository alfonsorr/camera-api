package org.alfiler.actors.config

import java.util.UUID

import akka.actor.{Actor, ActorRef, Cancellable, Props}
import org.alfiler.actors.config.PhotoConfigurator.{CameraFlowConfig, NoConfigSetted}
import org.alfiler.actors.deliverer.Deliverer
import org.alfiler.actors.paparazzi.Paparazzi
import org.alfiler.{Photo, PhotoOptions, PhotoPublic}

import scala.concurrent.ExecutionContextExecutor


object PhotoConfigurator {
  import scala.concurrent.duration._
  case class CameraFlowConfig(photoPeriod:FiniteDuration = 1.second, room:String = UUID.randomUUID().toString.take(10), group:String = "default")
  case object NoConfigSetted

  def props():Props = Props(new PhotoConfigurator)
}

class PhotoConfigurator extends Actor{
  import scala.concurrent.duration._

  type PhotoTransformer = Photo => PhotoPublic

  private val camera:ActorRef = context.actorOf(Paparazzi.props(PhotoOptions()))
  private val deliverer:ActorRef = context.actorOf(Deliverer.props())
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  def schedulePhotos(actualConfig: CameraFlowConfig):Cancellable = {
    context.system.scheduler.schedule(0.seconds,actualConfig.photoPeriod,camera, Paparazzi.SnapPhoto)
  }

  def photoToPublic(actualConfig: CameraFlowConfig):PhotoTransformer = { photo:Photo =>
    PhotoPublic(photo.date,photo.data,photo.format,actualConfig.room,actualConfig.group)
  }

  override def receive: Receive = {
    case initialConfig:CameraFlowConfig =>
      context.become(processWithConfig(initialConfig, photoToPublic(initialConfig), schedulePhotos(initialConfig)))
    case _ => sender() ! NoConfigSetted

  }

  def processWithConfig(actualConfig: CameraFlowConfig, transform:PhotoTransformer, sender:Cancellable):Receive = {
    case p:Photo => deliverer ! transform(p)
    case newConfig:CameraFlowConfig =>
      val updatedSender = if (newConfig.photoPeriod != actualConfig.photoPeriod) {
        sender.cancel()
        schedulePhotos(newConfig)
      } else {
        sender
      }
      context.become(processWithConfig(newConfig, photoToPublic(newConfig), updatedSender))
  }
}
