package ru.otus.sc.book.dao

import java.util.UUID

import scala.concurrent.Future
import ru.otus.sc.book.model.Book

trait BookDao {
  def listBooks: Future[Vector[Book]]
  def getBook(id: UUID): Future[Option[Book]]
  def createBook(book: Book): Future[Option[Book]]
  def updateBook(book: Book): Future[Option[Book]]
  def deleteBook(id: UUID): Future[Option[Book]]
}
