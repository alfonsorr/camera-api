package org.alfiler.actors.config

import java.util.Calendar

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.testkit.TestProbe
import org.alfiler.actors.config.PhotoConfigurator.{
  CameraFlowConfig,
  NoConfigSetted
}
import org.alfiler.{Photo, PhotoFormats, PhotoPublic}
import testUtils.AkkaTestUtil

class PhotoConfiguratorTest extends AkkaTestUtil {

  import scala.concurrent.duration._

  val testPhoto = Photo(Calendar.getInstance(), Array.empty, PhotoFormats.GIF)

  "photo configurator" should "reply with NoConfigSetted if it's not configured" in {
    val testActor = system.actorOf(PhotoConfigurator.props())
    val prove = TestProbe()
    within(1.seconds) {
      testActor.tell(testPhoto, prove.testActor)
      prove.expectMsg(NoConfigSetted)
    }
  }

  it should "acept a first config and set the photos with that config" in {
    val testActor = system.actorOf(PhotoConfigurator.props())
    val testRoomTopic = "room"
    val testGroupTopic = "group"
    val config = CameraFlowConfig(room = testRoomTopic, group = testGroupTopic)
    val mediator = DistributedPubSub(system).mediator

    val roomProbe = TestProbe()
    val groupProbe = TestProbe()

    mediator ! Subscribe(testRoomTopic, roomProbe.ref)
    mediator ! Subscribe(testGroupTopic, groupProbe.ref)

    val testResultphoto = PhotoPublic(testPhoto.date,
                                      testPhoto.data,
                                      testPhoto.format,
                                      testRoomTopic,
                                      testGroupTopic)
    within(1.seconds) {
      testActor ! config
      testActor ! testPhoto
      roomProbe.expectMsg(testResultphoto)
      groupProbe.expectMsg(testResultphoto)
    }
  }

  it should "snap photos in the period passed" in {
    val testActor = system.actorOf(PhotoConfigurator.props())
    val testRoomTopic = "room"
    val testGroupTopic = "group"
    val config = CameraFlowConfig(room = testRoomTopic, group = testGroupTopic)
    val mediator = DistributedPubSub(system).mediator

    val roomProbe = TestProbe()
    val groupProbe = TestProbe()

    mediator ! Subscribe(testRoomTopic, roomProbe.ref)
    mediator ! Subscribe(testGroupTopic, groupProbe.ref)

    var roomMessages = Seq[PhotoPublic]()
    var groupMessages = Seq[PhotoPublic]()

    testActor ! config

    roomProbe.receiveWhile(4 * config.photoPeriod) {
      case a: PhotoPublic => roomMessages = a +: roomMessages
    }
    groupProbe.receiveWhile(1 * config.photoPeriod) {
      case a: PhotoPublic => groupMessages = a +: groupMessages
    }
    roomMessages.length should be > 3
    groupMessages.length should be > 4
  }
}
