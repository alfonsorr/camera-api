package actors

import actors.NexusCamerasList.{CameraNotFound, GetListOfCameras, GetPhotoFromList}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import messages.GetPhoto


object NexusCamerasList {
  case class GetPhotoFromList(id:String, a:GetPhoto)
  case object CameraNotFound
  case object GetListOfCameras
  def props():Props = Props[NexusCamerasList]
}

class NexusCamerasList extends Actor with ActorLogging{

  var cameras = Map.empty[String,ActorRef]

  override def receive: Receive = {
    case GetPhotoFromList(id, action) => if (cameras.contains(id)) {
      log.info(s"forwarding to the camera $id")
      cameras(id) forward action
    } else {
      log.error(s"camera $id not found")
      sender() ! CameraNotFound
    }
    case GetListOfCameras =>
      log.info("sending the list of cameras :D")
      sender() ! cameras.keys.toList
    case CameraUp(id, ref) =>
      log.info(s"new camera $id")
      cameras = cameras + (id -> ref)
  }
}
