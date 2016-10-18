package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import messages.GetLastPhoto

trait CameraState {
}
case class CameraUp(idCamera: String, ref:ActorRef) extends CameraState
case class CameraDown(pathOfCamera: String) extends CameraState
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
    receiveCamera(Map.empty,Map.empty)
  def receiveCamera(cameras: Map[String, ActorRef], senders: Map[ActorRef, String]): Receive = {
    case CameraUp(id,ref) =>
      val actualCameras = cameras + (id -> ref)
      val refs = senders + (sender() -> id)
      context.become(receiveCamera(actualCameras, refs), discardOld = true)
      ref ! GetLastPhoto
      log.info(s"""new camera "$id" added :D ${ref.path}""")
    case CameraDown(path) =>
      //context.become(receiveCamera(cameras - id))
      log.info("we lost a camera!!!!!!")
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      if (member.roles.contains("camera")) {
        log.info(s"Expecting the registry of a camera ${member.uniqueAddress}")
      }
    case UnreachableMember(member) =>
      if (member.roles.contains("camera")) {
        log.info("Member detected as unreachable: {}", member)
        unregisterCamera("camera")
        cluster.down(member.address)
      }
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }

  def unregisterCamera(path:String) = {
    self ! CameraDown(path)
  }
}
