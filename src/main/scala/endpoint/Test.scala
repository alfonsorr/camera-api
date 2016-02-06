package endpoint

import javax.ws.rs.Path

import io.swagger.annotations.{ApiImplicitParam, ApiImplicitParams, ApiOperation, Api}
import akka.http.scaladsl.server.Directives._

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
      complete("this is a test")
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
