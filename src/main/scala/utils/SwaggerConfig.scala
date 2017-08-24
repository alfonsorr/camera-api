package utils

import com.github.swagger.akka.SwaggerHttpService
import endpoint.PhotoEndpoint


/**
  * Created by Alfonso on 06/02/2016.
  */
class SwaggerConfig() extends SwaggerHttpService {
  override val apiClasses = Set(classOf[PhotoEndpoint])
  override val host = "localhost:8080"
}