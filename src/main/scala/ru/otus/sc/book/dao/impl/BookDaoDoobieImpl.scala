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
import ru.otus.sc.author.model.Genre
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
      b <- selectBook(id)
    } yield b.map(_.toBook))
      .transact(tr)
      .unsafeToFuture()
  }

  private def selectBook(id: UUID): ConnectionIO[Option[BookRow]] = {
    sql"""select b.id, b.name, a.name, b.published_year, b.pages_count, g.name from books b 
           left join authors a on a.id = b.author_id 
           left join genres g on g.id = b.genre_id
           where b.id = $id
        """
      .query[BookRow]
      .option
  }
  private def insertBook(book: Book, authorId: Option[UUID], genreId: Option[UUID]) = {
    authorId match {
      case Some(aId) =>
        genreId match {
          case Some(gId) =>
            sql"""insert into
              books(name, author_id, genre_id, published_year, pages_count, created_at, updated_at) values
              (${book.name}, $aId, $gId, ${book.publishedYear}, ${book.pagesCount}, NOW(), NOW())
             """.update
              .withGeneratedKeys[UUID]("id")
              .compile
              .lastOrError

          case None => (UUID.randomUUID()).pure[ConnectionIO]
        }
      case None => (UUID.randomUUID()).pure[ConnectionIO]
    }
  }

  override def createBook(book: Book): Future[Option[Book]] = {
    val res = for {
      genreIdOption  <- genreIdIO(book.genre)
      authorIdOption <- authorIdIO(book.authorName)
      newId          <- insertBook(book, authorIdOption, genreIdOption)
    } yield Some(book.copy(id = Some(newId)))

    res.transact(tr).unsafeToFuture()
  }

  override def updateBook(book: Book): Future[Option[Book]] = {
    book match {
      case Book(Some(id), name, authorName, publishedYear, pagesCount, genre) =>
        val res = for {
          bookRow        <- selectBook(id)
          genreIdOption  <- genreIdIO(genre)
          authorIdOption <- authorIdIO(authorName)
          _ <- genreIdOption match {
            case Some(gId) =>
              authorIdOption match {
                case Some(aId) =>
                  sql"""
                       update books set author_id = $aId, genre_id = $gId, name = $name, published_year= $publishedYear, pages_count=$pagesCount
                       where id = $id
                     """.update.run
                case None => None.pure[ConnectionIO]
              }
            case None => None.pure[ConnectionIO]
          }
        } yield bookRow.map(_ => book)

        res.transact(tr).unsafeToFuture()
      case _ => Future.successful(None)
    }
  }

  override def deleteBook(id: UUID): Future[Option[Book]] = {
    val res = for {
      bookRow <- selectBook(id)
      _ <- bookRow match {
        case Some(_) =>
          sql"""delete from books where id = $id""".update.run
        case None => ().pure[ConnectionIO]
      }
    } yield bookRow.map(_.toBook)

    res.transact(tr).unsafeToFuture()
  }

  def deleteAll(): Future[Int] = sql"delete from books".update.run.transact(tr).unsafeToFuture()

  private def genreIdIO(genre: Genre): ConnectionIO[Option[UUID]] =
    sql"""select id from genres where name = ${stringFromGenre(genre)}""".query[UUID].option

  private def authorIdIO(name: String): ConnectionIO[Option[UUID]] =
    sql"""select id from authors where name = $name""".query[UUID].option
}
