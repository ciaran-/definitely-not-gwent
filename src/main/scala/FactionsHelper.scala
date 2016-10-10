import com.typesafe.config.ConfigFactory
import BetterPostgresDriver.api._
import scala.concurrent.Future


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

}

case class Faction(id: Int, name: String, description: String, logo: String)

class FactionsTable(tag: Tag) extends Table[Faction](tag, "factions"){
  def id = column[Int]("id", O.AutoInc)
  def name = column[String]("name")
  def description = column[String]("description")
  def img = column[String]("img")


  def * =(id, name, description, img) <> ((Faction.apply _).tupled, Faction.unapply)
}