package camera

import java.io.{ByteArrayOutputStream, InputStream}
import java.nio.file.{Files, Paths}

import com.typesafe.scalalogging.LazyLogging
import utils.Configuration

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

sealed trait PhotoFormat {
  def apply(): String
}

object JPG extends PhotoFormat {
  def apply(): String = "jpg"
}

object GIF extends PhotoFormat {
  def apply(): String = "gif"
}

object PNG extends PhotoFormat {
  def apply(): String = "png"
}

object BMP extends PhotoFormat {
  def apply(): String = "bmp"
}

object Camera extends LazyLogging with Configuration {

  val raspistillPath = "/opt/vc/bin/raspistill"
  val timeout = config.getDuration("timeout").toMillis

  private def snap(photoOptions: PhotoOptions, name: String)(
      implicit executor: ExecutionContext): Future[Array[Byte]] = {
    Future {
      import scala.sys.process._
      val str = photoOptions(name)
      val command = s"$raspistillPath $str"
      logger.info(s"Executing: $command")
      val os = new ByteArrayOutputStream()
      val result = command #> os
      result.!
      os.toByteArray
    }
  }

  def takePicture(photoOptions: PhotoOptions)(
      implicit executor: ExecutionContext): Future[Array[Byte]] = {
    snap(photoOptions, "-")
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
      transformedValue = transformValues(value)
    } yield (tag, transformedValue)

    list
      .filter(e => e._2.nonEmpty)
      .map { case (tag, value) => s"-$tag ${value.get}" }
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

case class PhotoOptions(path: String,
                        format: PhotoFormat = JPG,
                        width: Int = 1900,
                        height: Int = 1080,
                        quality: Int = 100,
                        timeout: Duration = Duration(100, "ms"),
                        advanceOptions: Option[AdvanceOptions] = None,
                        noPreview: Boolean = true)
    extends TaggedOptions {
  def apply(name: String): String = {
    val filename = if (name == "-"){
      name
    } else {
      filePath(name)
    }
    val values = List(Some(width),
                      Some(height),
                      Some(quality),
                      Some(filename),
                      Some(format()),
                      Some(timeout.toMillis),
                      Some(noPreview))
    val tags = List("w", "h", "q", "o", "e", "t", "n")
    stringOfTags(values, tags)
  }

  def filePath(name: String) = s"$path/$name.${format()}"
}
