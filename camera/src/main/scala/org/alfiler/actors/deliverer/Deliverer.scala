package org.alfiler.actors.deliverer

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import org.alfiler.{Photo, PhotoPublic}
import org.alfiler.actors.deliverer.Deliverer.{SendTo, StopSendingTo}

object Deliverer {
  def props() = Props(new Deliverer)
  case class SendTo(topic:String)
  case class StopSendingTo(topic:String)
}

class Deliverer extends Actor{

  private val mediator = DistributedPubSub(context.system).mediator


  def sendToAllRegistered(sendTo:Vector[String]):Receive = {
    case a:PhotoPublic =>
      mediator ! Publish(a.room, a)
      mediator ! Publish(a.group, a)
      sendTo.foreach(mediator ! Publish(_,a))
    case SendTo(topic) => context.become(sendToAllRegistered(sendTo :+ topic))
    case StopSendingTo(topic) => context.become(sendToAllRegistered(sendTo.filter( _ != topic)))
  }

  override def receive: Receive = sendToAllRegistered(Vector.empty)
}
