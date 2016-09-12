package messages

import java.util.Calendar

case class Photo(date:Calendar, data:Array[Byte])

object GetLastPhoto

case class GetPhoto(pos:Int)