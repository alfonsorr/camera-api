package org.alfiler.actors.paparazzi

import java.util.Calendar

import akka.actor.{Actor, ActorRef, Props}
import akka.testkit.TestProbe
import org.alfiler.actors.paparazzi.Paparazzi.SnapPhoto
import org.alfiler.camera.Camera
import org.alfiler.{Photo, PhotoOptions}
import testUtils.AkkaTestUtil

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class PaparazziTest extends AkkaTestUtil{

  def childTester(props:Props)(f:(TestProbe, ActorRef) => Unit):Unit = {
    val proxy = TestProbe()
    val parent = system.actorOf(Props(new Actor {
      private val child = context.actorOf(props, "child")

      def receive: PartialFunction[Any, Unit] = {
        case x if sender == child => proxy.ref forward x
        case x => child forward x
      }
    }))
    f(proxy,parent)
    system.stop(parent)
  }

  "The paparazzi actor" should "ask for a photo with the given options" in {

    val options = PhotoOptions()

    class CameraTest extends Camera {

      var lastPhotoOptions:Option[PhotoOptions] = None

      override def takePicture(photoOptions: PhotoOptions)(implicit executor: ExecutionContext): Future[Photo] = {
        lastPhotoOptions = Some(photoOptions)
        Future.successful(Photo(Calendar.getInstance(), Array.empty[Byte], photoOptions.format))
      }
    }

    val cameraTest = new CameraTest()

    childTester(Paparazzi.props(options, cameraTest)) {
      (proxy, testActor) =>
      within(2.seconds) {
        testActor ! SnapPhoto
        proxy.expectMsgClass(classOf[Photo])
        cameraTest.lastPhotoOptions should be(Some(options))
      }
    }

  }
}
