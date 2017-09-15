package actors.paparazzi

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSub
import akka.pattern.pipe
import camera.{Camera, PhotoOptions}

case object SnapPhoto

object Paparazzi {
  def props(photoOptions: PhotoOptions, photoDestinyActor:ActorRef, id:String, camera: Camera = Camera.defaultCamera) =
    Props(new Paparazzi(photoOptions, photoDestinyActor, id, camera))
}

class Paparazzi(photoOptions: PhotoOptions, photoDestinyActor:ActorRef, id:String, camera: Camera) extends Actor with ActorLogging{

  import context.dispatcher

  private val mediator = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case SnapPhoto => camera.takePicture(photoOptions).map(p => Publish) pipeTo photoDestinyActor
  }
}
