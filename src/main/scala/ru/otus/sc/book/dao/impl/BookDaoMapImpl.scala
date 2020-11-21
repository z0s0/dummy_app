package ru.otus.sc.book.dao.impl

import java.util.UUID

import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model.Book

import scala.concurrent.{ExecutionContext, Future}

class BookDaoMapImpl(implicit val ThreadPool: ExecutionContext) extends BookDao {
  private var books = Map[UUID, Book]()

  override def listBooks: Future[Vector[Book]]         = Future(books.values.toVector)
  override def getBook(id: UUID): Future[Option[Book]] = Future(books.get(id))

  override def updateBook(book: Book): Future[Option[Book]] =
    Future {
      if (isValidBook(book)) {
        for {
          id <- book.id
          _  <- books.get(id)
        } yield {
          books += (id -> book)
          book
        }
      } else None
    }

  override def createBook(book: Book): Future[Option[Book]] =
    Future {
      if (isValidBook(book)) {
        val id         = book.id.getOrElse(UUID.randomUUID())
        val bookWithID = book.copy(id = Some(id))
        books += (id -> bookWithID)
        Some(bookWithID)
      } else None
    }

  override def deleteBook(id: UUID): Future[Option[Book]] =
    Future {
      books.get(id) match {
        case book @ Some(_) =>
          books -= id
          book

        case None => None
      }
    }

  private def isValidBook(book: Book): Boolean = true
}
