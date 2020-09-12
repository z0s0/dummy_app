package ru.otus.sc.book.dao

import java.util.UUID

import ru.otus.sc.book.model.Book

trait BookDao {
  def createBook(book: Book): Book
  def updateBook(book: Book): Option[Book]
  def getBook(id: UUID): Option[Book]
  def deleteBook(id: UUID): Option[Book]
  def listBooks(): Seq[Book]
}
