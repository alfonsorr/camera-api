package actors.photoGetter

import java.util.Calendar

import org.alfiler.camera.PhotoFormats
import messages.{GetLastPhoto, GetNthPhoto, Photo}
import testUtils.AkkaTestUtil

class PhotoCacheTest extends AkkaTestUtil{
import scala.concurrent.duration._
  "PhotoCache" should "save the last photo sended" in {
    val cacheTest = system.actorOf(PhotoCache.props(5))
    within(2.seconds){
      val testPhoto = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto
      cacheTest ! GetLastPhoto
      expectMsg(testPhoto)
      val testPhotoNewer = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhotoNewer
      cacheTest ! GetLastPhoto
      expectMsg(testPhotoNewer)
    }
  }
  it should "give the nth phto" in {
    val cacheTest = system.actorOf(PhotoCache.props(5))
    within(2.seconds){
      val testPhoto1 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto1
      val testPhoto2 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto2
      val testPhoto3 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto3
      val testPhoto4 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto4
      cacheTest ! GetNthPhoto(3)
      expectMsg(testPhoto1)
      cacheTest ! GetNthPhoto(2)
      expectMsg(testPhoto2)
      cacheTest ! GetNthPhoto(1)
      expectMsg(testPhoto3)
      cacheTest ! GetNthPhoto(0)
      expectMsg(testPhoto4)
    }
  }
  it should "give the last photo if the index is not inserted" in {
    val cacheTest = system.actorOf(PhotoCache.props(5))
    within(2.seconds){
      val testPhoto1 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto1
      val testPhoto2 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto2
      val testPhoto3 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto3
      val testPhoto4 = Photo(Calendar.getInstance(),Array.empty[Byte], PhotoFormats.JPG)
      cacheTest ! testPhoto4
      cacheTest ! GetNthPhoto(30)
      expectMsg(testPhoto1)
    }
  }

  it should "not return anything if it doesnt have photos inserted" in {
    val cacheTest = system.actorOf(PhotoCache.props(5))
    within(20.seconds){
      cacheTest ! GetNthPhoto(30)
      expectNoMsg(3.seconds)
    }
  }

}
