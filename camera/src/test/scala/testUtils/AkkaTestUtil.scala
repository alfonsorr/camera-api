package testUtils

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestKitBase}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

trait AkkaTestUtil extends TestKitBase with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  implicit lazy val system: ActorSystem = ActorSystem("akkaTest-"+this.getClass.getSimpleName, ConfigFactory.load(this.getClass.getSimpleName+".conf"))
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

}
