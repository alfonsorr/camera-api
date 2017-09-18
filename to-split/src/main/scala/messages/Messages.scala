package messages

import java.util.Calendar

trait GetPhoto

case object GetLastPhoto extends GetPhoto

case class GetNthPhoto(pos:Int) extends GetPhoto