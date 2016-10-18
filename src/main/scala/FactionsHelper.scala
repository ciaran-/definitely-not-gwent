import com.typesafe.config.ConfigFactory
import BetterPostgresDriver.api._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import spray.json.PrettyPrinter
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success}


object FactionsHelper {

  implicit val executionContext = APIService.executor

  val dbConfig = ConfigFactory.load().getConfig("postgres")
  val dbLocation = dbConfig.getString("location")
  val dbName = dbConfig.getString("database")
  val db = Database.forURL(s"jdbc:postgresql://$dbLocation/$dbName", driver = "org.postgresql.Driver")

  val clinics = TableQuery[FactionsTable]

  def listAll(): Future[Seq[Faction]] = {
    db.run(clinics.result).map(_.map(x => x))
  }

  def factionById(id: Int): Future[Option[Faction]] = {
    db.run(
      clinics.filter(_.id === id).result).map(_.headOption)
  }

}

case class Faction(id: Int, name: String, description: String, logo: String)

class FactionsTable(tag: Tag) extends Table[Faction](tag, "factions"){
  def id = column[Int]("id", O.AutoInc)
  def name = column[String]("name")
  def description = column[String]("description")
  def img = column[String]("img")


  def * =(id, name, description, img) <> ((Faction.apply _).tupled, Faction.unapply)
}

trait FactionsRouting extends DefaultJsonProtocol {
  implicit val factionFormat = jsonFormat4(Faction)

  val factionsRouting =
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
      } ~ {
        path(IntNumber) { factionId =>
          onComplete(FactionsHelper.factionById(factionId)) {
            case Success(maybeFaction) =>
              complete {
                maybeFaction match {
                  case Some(faction) =>
                    HttpResponse(entity = HttpEntity(PrettyPrinter(faction.toJson)))
                  case None =>
                    HttpResponse(StatusCodes.NotFound, entity = s"Faction $factionId Not Found")
                }
              }
            case Failure(exc) => complete(HttpResponse(StatusCodes.InternalServerError, entity = s"An error occurred ${exc.getMessage}"))
          }
        }
      }
    }
}