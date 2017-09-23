package mains

import org.scalatest.{FlatSpecLike, Matchers}

class BootTest extends FlatSpecLike with Matchers{



  "Boot class" should "start the options passed and rejected unknown" in {
    val algo = new BootInterface() {
      override val options: PartialFunction[String, String] = {
        case "option1" => "TEST1"
        case "option2" => "TEST2"
      }
    }
    algo.execute(Array("option1")) should be(Right("TEST1"))
    algo.execute(Array("option2")) should be(Right("TEST2"))
    algo.execute(Array("option3")) should be(Left("option not recogniced"))
    algo.execute(Array.empty[String]) should be(Left("first argument is needed"))
  }
}
