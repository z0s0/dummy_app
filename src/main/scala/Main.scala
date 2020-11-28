import cats.implicits._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.otus.sc.author.dao.impl.AuthorDaoDoobieImpl
import ru.otus.sc.author.service.impl.AuthorServiceImpl
import ru.otus.sc.author.route.AuthorRouter
import ru.otus.sc.book.dao.impl.BookDaoDoobieImpl
import ru.otus.sc.book.route.BookRouter
import ru.otus.sc.book.service.impl.BookServiceImpl
import cats.effect.{Blocker, ContextShift, IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import ru.otus.sc.Config
import ru.otus.sc.db.Migrations
import ru.otus.sc.author.route.AuthorRoutesDocs
import ru.otus.sc.book.route.BookRoutesDocs

import scala.concurrent.ExecutionContext
import scala.io.StdIn
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.docs.openapi._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

object Main {
  def createRoute(tr: Transactor[IO])(implicit ec: ExecutionContext): Route = {
    val authorDao = new AuthorDaoDoobieImpl(tr)
    val bookDao   = new BookDaoDoobieImpl(tr)

    val authorService = new AuthorServiceImpl(authorDao)
    val bookService   = new BookServiceImpl(bookDao)

    val combinedRoutes = BookRoutesDocs.routes ++ AuthorRoutesDocs.routes

    val yamlDocs = combinedRoutes.toOpenAPI("Otus app", "0.1").toYaml

    val authorRouter = new AuthorRouter(authorService)

    val bookRouter = new BookRouter(bookService)

    authorRouter.route ~ bookRouter.route ~ (new SwaggerAkka(yamlDocs).routes)
  }

  def main(args: Array[String]): Unit = {
    implicit val sc: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    def makeBinding(tr: Transactor[IO])(implicit system: ActorSystem) = {
      Resource.make(
        IO.fromFuture(
          IO(
            Http()(system)
              .newServerAt("localhost", 5000)
              .bind(createRoute(tr)(system.dispatcher))
          )
        )
      )(b => IO.fromFuture(IO(b.unbind())).map(_ => ()))
    }
    val config = Config.default

    val binding =
      for {
        ce <- ExecutionContexts.fixedThreadPool[IO](32)
        be <- Blocker[IO]
        xa <- HikariTransactor.newHikariTransactor[IO](
          "org.postgresql.Driver",
          config.dbUrl,
          config.dbUser,
          config.dbPassword,
          ce,
          be
        )

        system <- Resource.make(IO(ActorSystem("system")))(s =>
          IO.fromFuture(IO(s.terminate())).map(_ => ())
        )

        binding <- makeBinding(xa)(system)
      } yield binding

    val app =
      binding
        .use { _ =>
          for {
            _ <- IO(println(s"Binding on localhost:5000"))
            _ <- IO(StdIn.readLine())
          } yield ()
        }

    val init = IO(new Migrations(config).applyMigrationsSync())

    app.unsafeRunSync()
  }
}
