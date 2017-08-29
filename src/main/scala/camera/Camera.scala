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

trait TaggedOptions {
  def stringOfTags(values: List[Option[Any]], tags: List[String]): String = {
    def transformValues(value: Option[Any]): Option[String] = value flatMap {
      case b: Boolean if b => Some("")
      case b: Boolean => None
      case i: Int => Some(i.toString)
      case d: Long => Some(d.toString)
      case s: String => Some(s)
      case somethingUnknown =>
        println(somethingUnknown.toString); Some(somethingUnknown.toString)
    }

    val list = for {
      (tag, value) <- tags.zip(values)
      transformedValue = transformValues(value).map(a => (tag, a))
    } yield transformedValue

    list
      .flatten
      .map { case (tag, value) => s"-$tag $value" }
      .mkString(" ")
  }
}

case class AdvanceOptions(verticalFlip: Boolean = false,
                          horizontalFlip: Boolean = false)
    extends TaggedOptions {
  def apply(): String = {
    val values = List(Some(verticalFlip), Some(horizontalFlip))
    val tags = List("vf", "hf")
    stringOfTags(values, tags)
  }
}

object PhotoOptions {
  import scala.concurrent.duration._
  val MAX_WIDTH = 1900
  val MAX_HEIGHT = 1080
  val MAX_QUALITY = 100
  val MIN_TIMEOUT:Duration = 100.millis
}

case class PhotoOptions(format: PhotoFormat = PhotoFormats.JPG,
                        width: Int = PhotoOptions.MAX_WIDTH,
                        height: Int = PhotoOptions.MAX_HEIGHT,
                        quality: Int = PhotoOptions.MAX_QUALITY,
                        timeout: Duration = PhotoOptions.MIN_TIMEOUT,
                        advanceOptions: Option[AdvanceOptions] = None,
                        noPreview: Boolean = true)
    extends TaggedOptions {
  import PhotoOptions._
  assert(width <= MAX_WIDTH && width > 0, "The width should be between 1900 and 1 (inclusive)")
  assert(height <= MAX_HEIGHT && width > 0, "The height should be between 1080 and 1 (inclusive)")
  assert(quality <= MAX_QUALITY && quality > 0, "The quality should be between 100 and 1 (inclusive)")
  assert(timeout.toMillis >= 100, "A timeout smaller than 100 milliseconds is too small")

  val optionString: String = {
    val printOutputPath = "-"
    val values = List(Some(width),
                      Some(height),
                      Some(quality),
                      Some(printOutputPath),
                      Some(format.stringValue),
                      Some(timeout.toMillis),
                      Some(noPreview))
    val tags = List("w", "h", "q", "o", "e", "t", "n")
    stringOfTags(values, tags)
  }
}
