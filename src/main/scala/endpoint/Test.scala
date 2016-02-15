package endpoint

import javax.ws.rs.Path

import camera.{PhotoOptions, Camera}
import io.swagger.annotations.{ApiImplicitParam, ApiImplicitParams, ApiOperation, Api}
import akka.http.scaladsl.server.Directives._

import scala.util.Failure

/**
  * Created by Alfonso on 06/02/2016.
  */
@Api(tags = Array("test"))
@Path("/test")
case class Test(){

  val route = {
    path("test"){
      testWithGet ~
      testWithPost
    }
  }

  @ApiOperation(httpMethod = "GET", value = "Returns a pet based on ID", response = classOf[String])
  @ApiImplicitParams(Array())
  def testWithGet = {
    get {
      onComplete(Camera.takePicture(PhotoOptions("~","example"))){
        case Failure(e) => complete(s"error: ${e.getMessage} ${e.getStackTrace.toList.map(e => e.toString).mkString(" ")}")
        case _ => complete("all ok")
      }
    }
  }

  @ApiOperation(httpMethod = "POST", value = "Returns a pet based on ID", response = classOf[String])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "test", required = true, dataType = "string", paramType = "body", value = "ID of pet that needs to be fetched")
  ))
  def testWithPost = {
    post{
      entity(as[String]){ string => complete(s"test with $string")}
    }
  }
}
