import cats.implicits._
import akka.actor.typed.scaladsl.Behaviors
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
import akka.actor.typed.{ActorSystem, Behavior}
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.cluster.typed.Cluster
import akka.management.scaladsl.AkkaManagement
import ru.otus.sc.auth.AuthService

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.io.StdIn
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.docs.openapi._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

object Main {

  def createRoute(tr: Transactor[IO], authService: AuthService)(implicit
      ec: ExecutionContext
  ): Route = {
    val authorDao = new AuthorDaoDoobieImpl(tr)
    val bookDao   = new BookDaoDoobieImpl(tr)

    val authorService = new AuthorServiceImpl(authorDao)
    val bookService   = new BookServiceImpl(bookDao)

    val combinedRoutes = BookRoutesDocs.routes ++ AuthorRoutesDocs.routes

    val yamlDocs = combinedRoutes.toOpenAPI("Otus app", "0.1").toYaml

    val authorRouter = new AuthorRouter(authorService, authService)

    val bookRouter = new BookRouter(bookService, authService)

    authorRouter.route ~ bookRouter.route ~ (new SwaggerAkka(yamlDocs).routes)
  }

  def main(args: Array[String]): Unit = {
    implicit val sc: ContextShift[IO]                   = IO.contextShift(ExecutionContexts.synchronous)
    implicit val system: ActorSystem[RootActor.Command] = App.system

    App.start()

    val config = Config.default

    if (App.hasRole("bookshelf")) {
      val transactor =
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

        } yield xa

      transactor
        .use { tr =>
          implicit val disp: ExecutionContextExecutor = system.executionContext
          val authenticator                           = new AuthService

          val binding = Http().newServerAt("localhost", 5000).bind(createRoute(tr, authenticator))

          new Migrations(config).applyMigrationsSync()

          binding.foreach(_ => println("http server started"))

          StdIn.readLine()

          system.terminate()

          ().pure[IO]
        }
        .unsafeRunSync()
    } else {
      ().pure[IO].unsafeRunSync()
    }
  }
}
