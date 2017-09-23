package endpoint

import java.util.concurrent.TimeUnit
import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{Api, ApiImplicitParams, ApiOperation}
import messages.GetNthPhoto
import org.alfiler.Photo

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

@Api(tags = Array("get"))
@Path("/get")
case class PhotoEndpoint(photoCache:ActorRef) extends LazyLogging{

  implicit val timeout:Timeout = Timeout(Duration(2,TimeUnit.SECONDS))

  val route:Route = {
    path("get"){
      getPhoto
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Returns the last taked photo", response = classOf[String])
  @ApiImplicitParams(Array())
  def getPhoto:Route = {
    get {
      parameters("n".as[Int].?) {
        n =>
        onComplete((photoCache ? GetNthPhoto(n.getOrElse(0))).mapTo[Photo]) {
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
