import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.comcast.ip4s._
import config.{AppConfig, DbConfig}
import controller.CashTransactionController
import dao.CashTransactionSQL
import doobie.util.transactor.Transactor
import services.CashTransactionStorage
import tofu.logging.Logging
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.implicits._
import tofu.Delay

object Main extends IOApp {

  implicit val mainLogger: Logging[IO] = Logging.Make.plain[IO].byName("Main")
  implicit val delay: Delay[IO] = Delay.apply[IO]

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      config <- Resource.eval(AppConfig.load)
      transactor = Transactor.fromDriverManager[IO](
        config.db.driver,
        config.db.url,
        config.db.user,
        config.db.password
      )
      sql = CashTransactionSQL.make
      storage = CashTransactionStorage.make(sql, transactor)
      controller = CashTransactionController.make(storage)
      routes = Http4sServerInterpreter[IO]().toRoutes(
        List(
          controller.listAllTransactions,
          controller.findTransactionById,
          controller.removeTransactionById,
          controller.createTransaction
        )
      )
      httpApp = Router("/" -> routes).orNotFound
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(
          Ipv4Address.fromString(config.server.host).getOrElse(ipv4"0.0.0.0")
        )
        .withPort(Port.fromInt(config.server.port).getOrElse(port"80"))
        .withHttpApp(httpApp)
        .build
    } yield ()).use { _ =>
      IO.pure(ExitCode.Success)
    }
}
