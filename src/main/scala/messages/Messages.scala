package messages

import java.util.Calendar

import camera.PhotoFormat

case class Photo(date:Calendar, data:Array[Byte], format:PhotoFormat)

trait GetPhoto

case object GetLastPhoto extends GetPhoto

case class GetNthPhoto(pos:Int) extends GetPhoto