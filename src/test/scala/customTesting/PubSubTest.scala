package customTesting

import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import com.typesafe.config.ConfigFactory

object SubActor {
  def props() = Props(new SubActor())
}

class SubActor extends Actor {

  private val mediator = DistributedPubSub(context.system).mediator

  mediator ! Subscribe("content", self)

  override def receive = {
    case a => println(a)
  }
}

object SubTest {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("ClusterSystem", ConfigFactory.load("sub.conf"))
    system.actorOf(SubActor.props())
  }
}


object PubActor {
  def props() = Props(new PubActor())
}

class PubActor extends Actor {
  val mediator = DistributedPubSub(context.system).mediator

  import akka.cluster.pubsub.DistributedPubSubMediator.Publish

  override def receive: Receive = {
    case msg => mediator ! Publish(topic = "content", msg)
  }
}

object PubTest {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("ClusterSystem", ConfigFactory.load("pub.conf"))

    val a = system.actorOf(PubActor.props())

    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global
    system.scheduler.schedule(0.seconds,1.seconds, a, "hola")
  }
}
