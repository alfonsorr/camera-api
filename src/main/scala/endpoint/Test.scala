package endpoint

import javax.ws.rs.Path

import akka.http.scaladsl.model.{MediaTypes,  HttpEntity, HttpResponse}
import camera.{PhotoOptions, Camera}
import io.swagger.annotations.{ApiImplicitParam, ApiImplicitParams, ApiOperation, Api}
import akka.http.scaladsl.server.Directives._

import scala.util.{Success, Failure}

/**
  * Created by Alfonso on 06/02/2016.
  */
@Api(tags = Array("test"))
@Path("/test")
case class Test(){

  val route = {
    path("test"){
      testWithGet
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Returns a pet based on ID", response = classOf[String])
  @ApiImplicitParams(Array())
  def testWithGet = {
    get {
      onComplete(Camera.takePicture(PhotoOptions("~","example"))){
        case Failure(e) => complete(s"error: ${e.getMessage} ${e.getStackTrace.toList.map(e => e.toString).mkString(" ")}")
        case Success(photo) => complete(HttpResponse(entity = HttpEntity(MediaTypes.`image/jpeg`, photo)))
      }
    }
  }
}
