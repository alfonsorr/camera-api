package camera

import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.concurrent.Future
import scala.concurrent.duration.Duration


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

object Camera extends LazyLogging{

  val raspistillPath = "/opt/vc/bin/raspistill"


  def takePicture(photoOptions: PhotoOptions): Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      println("taking a picture")
      logger.info("taking a picture")
      val str = photoOptions()
      println(s"options: $str")
      Runtime.getRuntime.exec(s"$raspistillPath $str")
      println("going to wait")
      logger.info("going to wait")
      Thread.sleep(photoOptions.timeout.toMillis)
      println("woke up")
      logger.info("woke up")
      str
    }
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
      case somethingUnknown => println(somethingUnknown.toString); Some(somethingUnknown.toString)
    }

    val list = for {
      (tag, value) <- tags.zip(values)
      transformedValue = transformValues(value)
    } yield (tag, transformedValue)


    list.filter(e => e._2.nonEmpty).map { case (tag, value) => s"-$tag ${value.get}" }.mkString(" ")
  }
}

case class AdvanceOptions(verticalFlip: Boolean = false, horizontalFlip: Boolean = false) extends TaggedOptions {
  def apply(): String = {
    val values = List(Some(verticalFlip), Some(horizontalFlip))
    val tags = List("vf", "hf")
    stringOfTags(values, tags)
  }
}

case class PhotoOptions(path: String, name: String, format: PhotoFormat = JPG, width: Int = 1024, height: Int = 728, quality: Int = 100, timeout: Duration = Duration(5, "sec"), advanceOptions: Option[AdvanceOptions] = None) extends TaggedOptions {
  def apply(): String = {
    val fullName = s"$path/$name.${format()}"
    val values = List(Some(width), Some(height), Some(quality), Some(fullName), Some(format()), Some(timeout.toMillis))
    val tags = List("w", "h", "q", "o", "e", "t")
    stringOfTags(values, tags)
  }
}