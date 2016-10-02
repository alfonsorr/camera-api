package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp, UnreachableMember}


object CameraClusterAware {
  def props(cache:ActorRef) = Props(new CameraClusterAware(cache))
}

class CameraClusterAware(cache:ActorRef) extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def receive: Receive = {
    case MemberUp(member) =>
      if (member.roles.contains("backend")) {
        val receptionActor = context.actorSelection(RootActorPath(member.address) / "user" / "creception")
        receptionActor ! CameraUp("myself", cache)
      }
  }
}
