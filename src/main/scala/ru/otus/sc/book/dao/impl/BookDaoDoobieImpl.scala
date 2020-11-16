package ru.otus.sc.book.dao.impl

import java.util.UUID

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie.util.transactor.Transactor
import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model.Book

import scala.concurrent.Future
import ru.otus.sc.ModelHelpers._
import ru.otus.sc.book.dao.impl.BookDaoDoobieImpl.BookRow

object BookDaoDoobieImpl {
  case class BookRow(
      id: UUID,
      name: String,
      authorName: String,
      publishedYear: Int,
      pagesCount: Int,
      genre: String
  ) {
    def toBook: Book =
      Book(
        id = Some(id),
        name = name,
        authorName = authorName,
        publishedYear = publishedYear,
        pagesCount = pagesCount,
        genre = genreFromString(genre)
      )
  }
}
class BookDaoDoobieImpl(tr: Transactor[IO]) extends BookDao {
  override def listBooks: Future[Vector[Book]] = {
    sql"""
         select b.id, b.name, a.name author_name, b.published_year, b.pages_count, g.name from books b 
         left join genres g on b.genre_id = g.id
         left join authors a on b.author_id = a.id
       """
      .query[BookRow]
      .map(_.toBook)
      .to[Vector]
      .transact(tr)
      .unsafeToFuture()
  }
  override def getBook(id: UUID): Future[Option[Book]] = {
    (for {
      b <- selectBook(id, forUpdate = false)
    } yield b.map(_.toBook))
      .transact(tr)
      .unsafeToFuture()
  }

  private def selectBook(id: UUID, forUpdate: Boolean): ConnectionIO[Option[BookRow]] = {
    val base =
      fr"""select b.id, b.name, a.name, b.published_year, b.pages_count, g.name from books b 
           left join authors a on a.id = b.author_id 
           left join genres g on g.id = b.genre_id
           where b.id = $id
        """

    val sql = if (forUpdate) base ++ fr" FOR UPDATE" else base

    sql
      .query[BookRow]
      .option
  }

  override def createBook(book: Book): Future[Option[Book]] = ???

  override def updateBook(book: Book): Future[Option[Book]] = ???
  override def deleteBook(id: UUID): Future[Option[Book]]   = ???
}
