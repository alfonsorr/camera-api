package utils

import com.github.swagger.akka.SwaggerHttpService
import endpoint.PhotoEndpoint

class SwaggerConfig() extends SwaggerHttpService {
  override val apiClasses = Set(classOf[PhotoEndpoint])
  override val host = "localhost:8080"
}