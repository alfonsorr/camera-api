package alfiler

import java.net.InetSocketAddress

import org.eclipse.californium.core.coap.Request
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore
import org.eclipse.californium.core.Utils
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.core.network.EndpointManager
import org.eclipse.californium.core.network.config.NetworkConfig
import org.eclipse.californium.scandium.DTLSConnector

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object Main {
  def main(args: Array[String]): Unit = {
    val CLIENT_IDENTITY: String = "Client_identity"
    val CLIENT_IDENTITY_SECRET: String = args(0)
    val IP_Adress = args(1)

    val request = Request.newGet()
    request.setURI(s"coaps://$IP_Adress:5684/15001/65537")
    request.setPayload("")
    request.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)

    val builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0))
    val pskStore = new InMemoryPskStore
    pskStore.addKnownPeer(new InetSocketAddress(request.getDestination, request.getDestinationPort), CLIENT_IDENTITY, new String(CLIENT_IDENTITY_SECRET).getBytes)
    builder.setPskStore(pskStore)
    builder.setSupportedCipherSuites(Array[CipherSuite](CipherSuite.TLS_PSK_WITH_AES_128_CCM_8))

    val dtlsconnector = new DTLSConnector(builder.build, null)

    val dtlsEndpoint = new CoapEndpoint(dtlsconnector, NetworkConfig.getStandard)
    dtlsEndpoint.start()
    EndpointManager.getEndpointManager.setDefaultSecureEndpoint(dtlsEndpoint)

    request.send()

    val response = request.waitForResponse()
    System.out.println(Utils.prettyPrint(response))
    dtlsEndpoint.stop()
  }
}

object MainPut {
  def main(args: Array[String]): Unit = {


    val CLIENT_IDENTITY: String = "Client_identity"
    val CLIENT_IDENTITY_SECRET: String = args(0)//$ $IP_Adress
    val IP_Adress = args(1)

    val request = Request.newPut()
    request.setURI(s"coaps://$IP_Adress:5684/15001/65538")
    request.setPayload("""{ "3311": [{ "5851": 200 }] }""")
    request.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)

    val builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0))
    val pskStore = new InMemoryPskStore
    pskStore.addKnownPeer(new InetSocketAddress(request.getDestination, request.getDestinationPort), CLIENT_IDENTITY, new String(CLIENT_IDENTITY_SECRET).getBytes)
    builder.setPskStore(pskStore)
    builder.setSupportedCipherSuites(Array[CipherSuite](CipherSuite.TLS_PSK_WITH_AES_128_CCM_8))

    val dtlsconnector = new DTLSConnector(builder.build, null)

    val dtlsEndpoint = new CoapEndpoint(dtlsconnector, NetworkConfig.getStandard)
    dtlsEndpoint.start()
    EndpointManager.getEndpointManager.setDefaultSecureEndpoint(dtlsEndpoint)

    val a = {
      request.send()

      val response = request.waitForResponse()
      System.out.println(Utils.prettyPrint(response))
    }
    val b = {
      println(dtlsEndpoint.isStarted)
      request.send()

      val response = request.waitForResponse()
      System.out.println(Utils.prettyPrint(response))
    }
    dtlsEndpoint.stop()
  }
}


object MainPutMultiple {
  def main(args: Array[String]): Unit = {
    val CLIENT_IDENTITY: String = "Client_identity"
    val CLIENT_IDENTITY_SECRET: String = args(0)//$ $IP_Adress
    val IP_Adress = args(1)

    val requestTest = Request.newPut()
    requestTest.setURI(s"coaps://$IP_Adress:5684/15001/65538")
    requestTest.setPayload("""{ "3311": [{ "5851": 200 }] }""")
    requestTest.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)

    val builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0))
    val pskStore = new InMemoryPskStore
    pskStore.addKnownPeer(new InetSocketAddress(requestTest.getDestination, requestTest.getDestinationPort), CLIENT_IDENTITY, new String(CLIENT_IDENTITY_SECRET).getBytes)
    builder.setPskStore(pskStore)
    builder.setSupportedCipherSuites(Array[CipherSuite](CipherSuite.TLS_PSK_WITH_AES_128_CCM_8))

    val dtlsconnector = new DTLSConnector(builder.build, null)

    val dtlsEndpoint = new CoapEndpoint(dtlsconnector, NetworkConfig.getStandard)
    dtlsEndpoint.start()
    EndpointManager.getEndpointManager.setDefaultSecureEndpoint(dtlsEndpoint)

    val step = 5

    for {
      _ <- 1 to 2
    } yield {
      for {
        n <- 1 to 250 by step
      } yield {
        val request = Request.newPut()
        request.setURI(s"coaps://$IP_Adress:5684/15001/65538")
        request.setPayload(s"""{ "3311": [{ "5851": $n }] }""")
        request.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)
        request.send()
        val response = request.waitForResponse()
        System.out.println(Utils.prettyPrint(response))
      }
      for {
        n <- 250 to 1 by -step
      } yield {
        val request = Request.newPut()
        request.setURI(s"coaps://$IP_Adress:5684/15001/65538")
        request.setPayload(s"""{ "3311": [{ "5851": $n }] }""")
        request.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)
        request.send()
        val response = request.waitForResponse()
        System.out.println(Utils.prettyPrint(response))
      }
    }

    dtlsEndpoint.stop()
  }
}


object MainPutOvni {
  def main(args: Array[String]): Unit = {
    val CLIENT_IDENTITY: String = "Client_identity"
    val CLIENT_IDENTITY_SECRET: String = args(0)//$ $IP_Adress
    val IP_Adress = args(1)

    val dummyRequest = Request.newPut()
    dummyRequest.setURI(s"coaps://$IP_Adress:5684/15001/65538")
    dummyRequest.setPayload("""{ "3311": [{ "5851": 200 }] }""")
    dummyRequest.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)

    val builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0))
    val pskStore = new InMemoryPskStore
    pskStore.addKnownPeer(new InetSocketAddress(dummyRequest.getDestination, dummyRequest.getDestinationPort), CLIENT_IDENTITY, new String(CLIENT_IDENTITY_SECRET).getBytes)
    builder.setPskStore(pskStore)
    builder.setSupportedCipherSuites(Array[CipherSuite](CipherSuite.TLS_PSK_WITH_AES_128_CCM_8))

    val dtlsconnector = new DTLSConnector(builder.build, null)

    val dtlsEndpoint = new CoapEndpoint(dtlsconnector, NetworkConfig.getStandard)
    dtlsEndpoint.start()
    EndpointManager.getEndpointManager.setDefaultSecureEndpoint(dtlsEndpoint)

    val step = 5

    def changeLight(bulb:Int, intensity:Int)(implicit executionContext: ExecutionContext):Future[Unit] = {
      Future {
        val request = Request.newPut()
        request.setURI(s"coaps://$IP_Adress:5684/15001/$bulb")
        request.setPayload(s"""{ "3311": [{ "5851": $intensity }] }""")
        request.getOptions.setContentFormat(MediaTypeRegistry.TEXT_PLAIN)
        request.send()
        val response = request.waitForResponse()
        System.out.println(Utils.prettyPrint(response))
      }
    }

    val initialIntens = List(1,10,200)
    val bulbs = (65541 to 65543).toList

    import scala.concurrent.ExecutionContext.Implicits.global

    (1 to 20).foldLeft(initialIntens)((intensity,_) => {
      println(intensity)
      println(bulbs)
      val allLightsChanged = intensity.zip(bulbs).map { case (int, bulb) => changeLight(bulb, int) }
        .reduce((a, b) => a.zip(b).map(_._1))
      Await.result(allLightsChanged,Duration.Inf)
      Thread.sleep(100)
      intensity.tail ++ List(intensity.head)
    }

    )



    dtlsEndpoint.stop()
  }
}
