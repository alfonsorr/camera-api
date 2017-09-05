package actors.paparazzi

import java.util.Calendar

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import camera.{Camera, PhotoOptions}
import com.typesafe.config.{Config, ConfigFactory}
import messages.Photo
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class PaparazziTest extends TestKit(ActorSystem("akkaTest"+classOf[PaparazziTest].getSimpleName, ConfigFactory.empty())) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  "The paparazzi actor" should "ask for a photo with the given options" in {

    val options = PhotoOptions()

    class CameraTest extends Camera {
      override def takePicture(photoOptions: PhotoOptions)(implicit executor: ExecutionContext): Future[Photo] = {
        photoOptions should be(options)
        Future.successful(Photo(Calendar.getInstance(), Array.empty[Byte], photoOptions.format))
      }
    }

    val destinyActor = TestProbe()
    val paparazziTest = system.actorOf(Paparazzi.props(options, destinyActor.ref, new CameraTest()))


    within(2.seconds){
      paparazziTest ! SnapPhoto
      destinyActor.expectMsgClass(classOf[Photo])
    }

  }
}
