import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.App
import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl
import ru.otus.sc.author.service.impl.AuthorServiceImpl
import ru.otus.sc.ThreadPool.CustomThreadPool
import ru.otus.sc.author.model.{Author, Genre}
import ru.otus.sc.author.route.AuthorRouter
import ru.otus.sc.book.dao.impl.BookDaoMapImpl
import ru.otus.sc.book.route.BookRouter
import ru.otus.sc.book.service.impl.BookServiceImpl

import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sys")

    import system.dispatcher

    val bookDao   = new BookDaoMapImpl
    val authorDao = new AuthorDaoMapImpl

    authorDao.createAuthor(
      Author(
        id = Some(UUID.randomUUID()),
        name = "ARmen",
        genres = Set(Genre.Programming)
      )
    )

    val authorService = new AuthorServiceImpl(authorDao, CustomThreadPool)
    val bookService   = new BookServiceImpl(bookDao, CustomThreadPool)

    val authorRouter = new AuthorRouter(authorService)
    val bookRouter   = new BookRouter(bookService)

    val rootRouter = authorRouter.route ~ bookRouter.route

    Http().newServerAt("localhost", 5000).bind(rootRouter)

    StdIn.readLine()

    system.terminate()
  }
}
