package ru.otus.sc.book.dao.impl
import java.util.UUID

import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model.Book

class BookDaoImpl extends BookDao {
  private var books: Map[UUID, Book] = Map.empty

  def getBook(id: UUID): Option[Book] = {
    books.get(id) match {
      case None => None
      case Some(book) =>
        books -= book.id
        Some(book)
    }
  }

  def createBook(book: Book): Book = {
    books += (book.id -> book)
    book
  }

  def listBooks(): Seq[Book] = books.values.toList

  def updateBook(book: Book): Option[Book] = {
    books.get(book.id) match {
      case Some(_) =>
        books += (book.id -> book)
        Some(book)
      case None =>
        None
    }
  }

  def deleteBook(id: UUID): Option[Book] = {
    getBook(id) match {
      case Some(book) =>
        books -= id
        Some(book)
      case None => None
    }
  }
}
