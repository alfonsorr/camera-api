package actors.photoGetter

import akka.actor.{Actor, ActorLogging, Props}
import messages.{GetLastPhoto, GetNthPhoto}
import org.alfiler.Photo

object PhotoCache {
  def props(nPhotos:Int):Props = Props(new PhotoCache(nPhotos))
}

class PhotoCache(nPhotos:Int) extends Actor with ActorLogging{
  override def receive: Receive = {
    case photo:Photo => context.become(receiveWithCache(List(photo)),discardOld = true)
    case _ => log.warning("not yet sended the first photo")
  }

  def receiveWithCache(photoList:List[Photo]): Receive = {
    case photo:Photo => log.info("new photo received")
      context.become(receiveWithCache(photo :: photoList),discardOld = true)
    case GetLastPhoto => log.info("sending the last photo")
      sender() ! photoList.head
    case GetNthPhoto(n) => log.info("sending the last photo")
      sender() ! photoList.lift(n).getOrElse(photoList.last)
  }

}
