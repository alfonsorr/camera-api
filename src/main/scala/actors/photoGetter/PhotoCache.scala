package actors.photoGetter

import akka.actor.{Actor, ActorLogging, Props}
import messages.{GetLastPhoto, GetPhoto, Photo}

object PhotoCache {
  def props(nPhotos:Int):Props = Props(new PhotoCache(nPhotos))
}

class PhotoCache(nPhotos:Int) extends Actor with ActorLogging{
  override def receive: Receive = {
    case photo:Photo => context.become(receiveWithCache(List(photo)))
  }

  def receiveWithCache(photoList:List[Photo]): Receive = {
    case photo:Photo => log.info("new photo received")
      context.become(receiveWithCache(photo :: photoList),discardOld = true)
    case GetLastPhoto => log.info("sending the last photo")
      sender() ! photoList.head
    case GetPhoto(n) => log.info("sending the last photo")
      sender() ! photoList.lift(n).getOrElse(photoList.last)
  }

}
