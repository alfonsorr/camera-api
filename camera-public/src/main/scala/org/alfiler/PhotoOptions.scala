package org.alfiler

import scala.concurrent.duration.Duration

object PhotoOptions {
  import scala.concurrent.duration._
  val MAX_WIDTH = 1900
  val MAX_HEIGHT = 1080
  val MAX_QUALITY = 100
  val MIN_TIMEOUT:Duration = 100.millis
}

case class PhotoOptions(format: PhotoFormats.JPG.type = PhotoFormats.JPG,
                        width: Int = PhotoOptions.MAX_WIDTH,
                        height: Int = PhotoOptions.MAX_HEIGHT,
                        quality: Int = PhotoOptions.MAX_QUALITY,
                        timeout: Duration = PhotoOptions.MIN_TIMEOUT,
                        noPreview: Boolean = true) {
  import PhotoOptions._
  assert(width <= MAX_WIDTH && width > 0, "The width should be between 1900 and 1 (inclusive)")
  assert(height <= MAX_HEIGHT && height > 0, "The height should be between 1080 and 1 (inclusive)")
  assert(quality <= MAX_QUALITY && quality > 0, "The quality should be between 100 and 1 (inclusive)")
  assert(timeout.toMillis >= 100, "A timeout smaller than 100 milliseconds is too small")

  val optionString: String = {
    def stringOfTags(values: List[Any], tags: List[String]): String = {
      def transformValues(value: Any): Option[String] = value match {
        case b: Boolean if b => Some("")
        case b: Boolean => None
        case i: Int => Some(i.toString)
        case d: Long => Some(d.toString)
        case s: String => Some(s)
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
    val printOutputPath = "-"
    val values = List(width,
      height,
      quality,
      printOutputPath,
      format.stringValue,
      timeout.toMillis,
      noPreview)
    val tags = List("w", "h", "q", "o", "e", "t", "n")
    stringOfTags(values, tags)
  }
}
