package actor.photoGetter

import akka.actor.Actor
import messages.Photo

class PhotoCache extends Actor{
  override def receive: Receive = {
    case photo:Photo => context.become(receiveWithCache(List(photo)))
  }

  def receiveWithCache(photoList:List[Photo]): Receive = {
    case photo:Photo => context.become(receiveWithCache(photo :: photoList),discardOld = true)
  }

}
