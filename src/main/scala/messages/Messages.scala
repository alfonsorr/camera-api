package messages

import java.util.Calendar

import camera.PhotoFormat

case class Photo(date:Calendar, data:Array[Byte], format:PhotoFormat)

object GetLastPhoto

case class GetPhoto(pos:Int)