package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import messages.GetLastPhoto

trait CameraState {
  val idCamera: String
}
case class CameraUp(idCamera: String, ref:ActorRef) extends CameraState
case class CameraDown(idCamera: String) extends CameraState
case class CameraManager(idCamera: String, ref:ActorRef)

object CamerasReception {
  def props() = Props(new CamerasReception())
}

class CamerasReception extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def receive: Receive =
    receiveCamera(Map.empty[String, CameraManager])
  def receiveCamera(cameras: Map[String, CameraManager]): Receive = {
    case CameraUp(id,ref) =>
      val newCamera = CameraManager(id,ref)
      context.become(receiveCamera(cameras + (id -> newCamera)))
      ref ! GetLastPhoto
      log.info(s"""new camera "$id" added :D""")
    case CameraDown(id) =>
      context.become(receiveCamera(cameras - id))
      log.info("we lost a camera!!!!!!")
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      if (member.roles.contains("camera")) {
        log.info("Expecting the registry of a camera")
      }
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      unregisterCamera("camera")
      cluster.down(member.address)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }

  def unregisterCamera(id:String) = {
    self ! CameraDown(id)
  }
}
