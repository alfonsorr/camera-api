package org.alfiler.camera

import org.alfiler.PhotoOptions
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class PhotoOptionsTest extends FlatSpec with Matchers{

  "Camera options" should "be created with the default options" in {
    PhotoOptions().optionString should be("-w 1900 -h 1080 -q 100 -o - -e jpg -t 100 -n ")
  }
  it should "change the with and height options" in {
    PhotoOptions(height = 500, width = 600).optionString should be ("-w 600 -h 500 -q 100 -o - -e jpg -t 100 -n ")
  }
  it should "change the timeout options" in {
    PhotoOptions(timeout = 300.seconds).optionString should be("-w 1900 -h 1080 -q 100 -o - -e jpg -t 300000 -n ")
  }
  it should "change the quality options" in {
    PhotoOptions(quality = 50).optionString should be("-w 1900 -h 1080 -q 50 -o - -e jpg -t 100 -n ")
  }
  it should "prevent from wrong widths" in {
    an[AssertionError] should be thrownBy PhotoOptions(width = -50).optionString
  }
  it should "prevent from wrong height" in {
    an[AssertionError] should be thrownBy PhotoOptions(height = -50).optionString
  }
  it should "prevent from small timeout" in {
    an[AssertionError] should be thrownBy PhotoOptions(timeout = 50.millis).optionString
  }
  it should "prevent from wrong quality" in {
    an[AssertionError] should be thrownBy PhotoOptions(quality = -50).optionString
  }
}
