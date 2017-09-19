package org.alfiler.actors.config

import akka.actor.{Actor, ActorRef, Cancellable}
import org.alfiler.actors.config.PhotoConfigurator.ActualConfig
import org.alfiler.actors.paparazzi.Paparazzi
import org.alfiler.{Photo, PhotoOptions, PhotoPublic}

import scala.concurrent.ExecutionContextExecutor


object PhotoConfigurator {
  import scala.concurrent.duration._
  private case class ActualConfig(photoPeriod:FiniteDuration = 1.second, room:String = "", group:String = "default")
  case class Periodiciy(photoPeriod:Duration)
}

class PhotoConfigurator extends Actor{
  import scala.concurrent.duration._

  type PhotoTransformer = Photo => PhotoPublic

  private val camera:ActorRef = context.actorOf(Paparazzi.props(PhotoOptions()))
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  def photoToPublic(actualConfig: ActualConfig):PhotoTransformer = {photo:Photo =>
    PhotoPublic(photo.date,photo.data,photo.format,actualConfig.room,actualConfig.group)
  }

  override def receive: Receive = {
    val conf = ActualConfig()
    processWithConfig(photoToPublic(conf), context.system.scheduler.schedule(0.seconds,conf.photoPeriod,camera, Paparazzi.SnapPhoto))
  }

  def processWithConfig(transform:PhotoTransformer, sender:Cancellable):Receive = {
    case p:Photo => transform(p)
  }
}
