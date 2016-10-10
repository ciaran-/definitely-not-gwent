import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol
import spray.json.PrettyPrinter
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

trait Protocols extends DefaultJsonProtocol {
  implicit val factionFormat = jsonFormat4(Faction)
}

trait Service extends Protocols{
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: ActorMaterializer

  def config: Config
  val logger: LoggingAdapter
}

object APIService extends App
                  with Service
                  with CorsSupport
{
  override implicit val system = ActorSystem()
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
        } ~
        pathPrefix("factions") {
          pathEndOrSingleSlash {
            onComplete(FactionsHelper.listAll()){
              case Success(factions) =>
                complete {
                  HttpResponse(entity = HttpEntity(PrettyPrinter(factions.toJson)))
                }
              case Failure(exc) =>
                complete {
                  HttpResponse(status = StatusCodes.InternalServerError, entity = HttpEntity(s"error: ${exc.getMessage}"))
                }
            }
          }
        }
      }
    }
  }

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}