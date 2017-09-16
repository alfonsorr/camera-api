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
  def props(camerasList:ActorRef) = Props(new CamerasReception(camerasList))
}

class CamerasReception(camerasList:ActorRef) extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def receive: Receive =
    receiveCamera(Map.empty,Map.empty)
  def receiveCamera(cameras: Map[String, ActorRef], senders: Map[ActorRef, String]): Receive = {
    case a:CameraState =>
      log.info("message from org.alfiler.camera")
      camerasList ! a
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      if (member.roles.contains("org.alfiler.camera")) {
        log.info(s"Expecting the registry of a org.alfiler.camera ${member.uniqueAddress}")
      }
    case UnreachableMember(member) =>
      if (member.roles.contains("org.alfiler.camera")) {
        log.info("Member detected as unreachable: {}", member)
        unregisterCamera("org.alfiler.camera")
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
