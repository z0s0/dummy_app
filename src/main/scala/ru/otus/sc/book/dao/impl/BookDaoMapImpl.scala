package ru.otus.sc.book.dao.impl

import ru.otus.sc.ThreadPool._

import java.util.UUID

import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model.Book

import scala.concurrent.Future

class BookDaoMapImpl extends BookDao {
  private var books = Map[UUID, Book]()

  override def listBooks: Future[Seq[Book]]            = Future(books.values.toSeq)
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
        val id         = UUID.randomUUID()
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
