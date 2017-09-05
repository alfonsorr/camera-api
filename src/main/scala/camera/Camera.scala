package camera

import java.io.ByteArrayOutputStream
import java.util.Calendar

import com.typesafe.scalalogging.LazyLogging
import messages.Photo

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

object Camera extends LazyLogging {

  val raspistillPath = "/opt/vc/bin/raspistill"

  private def snap(photoOptions: PhotoOptions)(
      implicit executor: ExecutionContext): Future[Array[Byte]] = {
    Future {
      import scala.sys.process._
      val str = photoOptions.optionString
      val command = s"$raspistillPath $str"
      logger.debug(s"Executing: $command")
      val os = new ByteArrayOutputStream()
      val process = command #> os
      process.!
      os.toByteArray
    }
  }

  def takePicture(photoOptions: PhotoOptions)(
      implicit executor: ExecutionContext): Future[Photo] = {
    snap(photoOptions).map(data => Photo(Calendar.getInstance,data, photoOptions.format))
  }
}

