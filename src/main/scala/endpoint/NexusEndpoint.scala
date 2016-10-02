package endpoint

import java.util.concurrent.TimeUnit
import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{Api, ApiImplicitParams, ApiOperation}
import messages.{GetPhoto, Photo}

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Created by Alfonso on 06/02/2016.
  */
@Api(tags = Array("get"))
@Path("/camera")
case class NexusEndpoint(photoCache:ActorRef) extends LazyLogging{

  implicit val timeout = Timeout(Duration(2,TimeUnit.SECONDS))

  val route = {
    path("camera"){
      cameraList
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Returns a list of active cameras", response = classOf[String])
  @ApiImplicitParams(Array())
  def cameraList = {
    get {
      parameters("n".as[Int].?) {
        n =>
        onComplete((photoCache ? GetPhoto(n.getOrElse(0))).mapTo[Photo]) {
          case Failure(e) =>
            logger.info("could't retreive the last photo")
            complete(s"error: ${e.getMessage} ${e.getStackTrace.toList.map(e => e.toString).mkString(" ")}")
          case Success(photo) =>
            logger.info("last photo correctly retreived")
            complete(HttpResponse(entity = HttpEntity(MediaTypes.`image/jpeg`, photo.data)))
        }
      }
    }
  }
}
