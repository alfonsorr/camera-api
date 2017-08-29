package endpoint

import java.util.concurrent.TimeUnit
import javax.ws.rs.Path

import actors.NexusCamerasList.{CameraNotFound, GetListOfCameras, GetPhotoFromList}
import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{Api, ApiImplicitParams, ApiOperation}
import messages.{GetNthPhoto, Photo}

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

@Api(tags = Array("get"))
@Path("/camera")
case class NexusEndpoint(camerasHandler: ActorRef) extends LazyLogging {

  implicit val timeout:Timeout = Timeout(Duration(2, TimeUnit.SECONDS))

  val route:Route = {
    pathPrefix("camera") {
      cameraPhoto ~ cameraList
    }
  }

  @ApiOperation(httpMethod = "GET",
                value = "Returns a list of active cameras",
                response = classOf[String])
  @ApiImplicitParams(Array())
  def cameraPhoto:Route = {
    pathPrefix(Segment) { cameraName =>
      get {
        parameters("n".as[Int].?) { n =>
          logger.info(s"asking for $cameraName")
          onComplete(
            camerasHandler ? GetPhotoFromList(cameraName,
                                              GetNthPhoto(n.getOrElse(0)))) {
            case Failure(e) =>
              logger.info("could't retreive the last photo")
              complete(
                s"error: ${e.getMessage} ${e.getStackTrace.toList.map(e => e.toString).mkString(" ")}")
            case Success(result) =>
              result match {
                case CameraNotFound =>
                  logger.error("photo correctly retreived")
                  complete(HttpResponse(404))
                case photo: Photo =>
                  logger.info("photo correctly retreived")
                  complete(HttpResponse(
                    entity = HttpEntity(MediaTypes.`image/jpeg`, photo.data)))
              }
          }
        }
      }
    }
  }
  def cameraList:Route = {
    pathEndOrSingleSlash {
      get {
        onComplete((camerasHandler ? GetListOfCameras).mapTo[List[String]]) {
          case Failure(e) =>
            logger.error("could't retreive the cameras list")
            complete(
              s"error: ${e.getMessage} ${e.getStackTrace.toList.map(e => e.toString).mkString(" ")}")
          case Success(list) =>
            complete(s"list of cameras: ${list.mkString(", ")}")
        }
      }
    }
  }
}
