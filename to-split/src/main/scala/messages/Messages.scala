package messages

import java.util.Calendar

import org.alfiler.camera.PhotoFormat

trait GetPhoto

case object GetLastPhoto extends GetPhoto

case class GetNthPhoto(pos:Int) extends GetPhoto