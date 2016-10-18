import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol


import scala.concurrent.ExecutionContextExecutor

trait Service {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: ActorMaterializer

  def config: Config
  val logger: LoggingAdapter
}

object APIService extends App
                  with Service
                  with CorsSupport
                  with FactionsRouting
{
  override implicit val system = ActorSystem("definitely-not-gwent-data")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  val routes = {
    corsHandler {
      get {
        pathEndOrSingleSlash{
          complete {
            HttpResponse(entity = HttpEntity(s"Hi! I'm an empty endpoint...for now."))
          }
        } ~ factionsRouting
      }
    }
  }

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}