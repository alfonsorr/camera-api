package endpoint

import javax.ws.rs.Path

import akka.http.scaladsl.model.{MediaTypes,  HttpEntity, HttpResponse}
import camera.{PhotoOptions, Camera}
import io.swagger.annotations.{ApiImplicitParams, ApiOperation, Api}
import akka.http.scaladsl.server.Directives._

import scala.util.{Success, Failure}

/**
  * Created by Alfonso on 06/02/2016.
  */
@Api(tags = Array("test"))
@Path("/test")
case class Test(){

  import scala.concurrent.ExecutionContext.Implicits.global

  val route = {
    path("testFast"){
      testFast
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Returns a pet based on ID", response = classOf[String])
  @ApiImplicitParams(Array())
  def testFast = {
    get {
      onComplete(Camera.takePicture(PhotoOptions(""))){
        case Failure(e) => complete(s"error: ${e.getMessage} ${e.getStackTrace.toList.map(e => e.toString).mkString(" ")}")
        case Success(photo) => complete(HttpResponse(entity = HttpEntity(MediaTypes.`image/jpeg`, photo)))
      }
    }
  }
}
