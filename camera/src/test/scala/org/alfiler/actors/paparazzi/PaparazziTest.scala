package org.alfiler.actors.paparazzi

import java.util.Calendar

import akka.testkit.TestProbe
import org.alfiler.camera.Camera
import org.alfiler.{Photo, PhotoOptions}
import testUtils.AkkaTestUtil

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class PaparazziTest extends AkkaTestUtil{

  "The paparazzi actor" should "ask for a photo with the given options" in {

    val options = PhotoOptions()

    class CameraTest extends Camera {

      var lastPhotoOptions:Option[PhotoOptions] = None

      override def takePicture(photoOptions: PhotoOptions)(implicit executor: ExecutionContext): Future[Photo] = {
        lastPhotoOptions = Some(photoOptions)
        Future.successful(Photo(Calendar.getInstance(), Array.empty[Byte], photoOptions.format))
      }
    }

    val destinyActor = TestProbe()
    val cameraTest = new CameraTest()
    val paparazziTest = system.actorOf(Paparazzi.props(options, destinyActor.ref, cameraTest))


    within(2.seconds){
      paparazziTest ! SnapPhoto
      destinyActor.expectMsgClass(classOf[Photo])
      cameraTest.lastPhotoOptions should be(Some(options))
    }

  }
}
