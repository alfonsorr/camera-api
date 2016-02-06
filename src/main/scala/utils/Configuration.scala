package utils

import com.typesafe.config.ConfigFactory

trait Configuration {
  lazy val config = ConfigFactory.load()
}
