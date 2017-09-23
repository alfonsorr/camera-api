package org.alfiler.actors.deliverer

import java.util.Calendar

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.testkit.TestProbe
import org.alfiler.actors.deliverer.Deliverer.{SendTo, StopSendingTo}
import org.alfiler.{PhotoFormats, PhotoPublic}
import testUtils.AkkaTestUtil

class DelivererTest extends AkkaTestUtil{

  import scala.concurrent.duration._

  "Deliverer" should "send to the room and group of the photo" in {
    val mediator = DistributedPubSub(system).mediator
    val testActor = system.actorOf(Deliverer.props())

    val testRoomTopic = "room"
    val testGroupTopic = "group"

    val roomProbe = TestProbe()
    val groupProbe = TestProbe()

    mediator ! Subscribe(testRoomTopic, roomProbe.ref)
    mediator ! Subscribe(testGroupTopic, groupProbe.ref)

    val testphoto = PhotoPublic(Calendar.getInstance(),Array.empty[Byte],PhotoFormats.JPG,testRoomTopic,testGroupTopic)


    within(1.seconds){
      testActor ! testphoto
      roomProbe.expectMsg(testphoto)
      groupProbe.expectMsg(testphoto)
    }

  }

  it  should "also send to the registered channels" in {
    val mediator = DistributedPubSub(system).mediator
    val testActor = system.actorOf(Deliverer.props())

    val testRoomTopic = "room"
    val testGroupTopic = "group"
    val testTopic = "test"

    val roomProbe = TestProbe()
    val groupProbe = TestProbe()
    val testProbe = TestProbe()

    mediator ! Subscribe(testRoomTopic, roomProbe.ref)
    mediator ! Subscribe(testGroupTopic, groupProbe.ref)
    mediator ! Subscribe(testTopic, testProbe.ref)

    val testphoto = PhotoPublic(Calendar.getInstance(),Array.empty[Byte],PhotoFormats.JPG,testRoomTopic,testGroupTopic)


    within(1.seconds){
      testActor ! SendTo("test")
      testActor ! testphoto
      roomProbe.expectMsg(testphoto)
      groupProbe.expectMsg(testphoto)
      testProbe.expectMsg(testphoto)
    }


    within(2.seconds){
      testActor ! StopSendingTo("test")
      testActor ! testphoto
      roomProbe.expectMsg(testphoto)
      groupProbe.expectMsg(testphoto)
      testProbe.expectNoMsg(1.seconds)
    }

  }
}
