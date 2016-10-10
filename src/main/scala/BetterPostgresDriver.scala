import com.github.tminglei.slickpg.{ExPostgresDriver, PgArraySupport, PgDate2Support}

trait BetterPostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDate2Support{
  override val api = BetterAPI
  object BetterAPI extends API
    with ArrayImplicits
    with DateTimeImplicits{
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    //    implicit val intListTypeMapper = new SimpleArrayJdbcType[Int]("integer").to(_.toList)
  }
}

object BetterPostgresDriver extends BetterPostgresDriver