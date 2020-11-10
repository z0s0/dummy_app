package ru.otus.sc.author.dao

import java.util.UUID

import ru.otus.sc.author.model.Author
import scala.concurrent.Future

trait AuthorDao {
  def listAuthors: Future[Seq[Author]]
  def getAuthor(id: UUID): Future[Option[Author]]
  def createAuthor(author: Author): Future[Option[Author]]
  def updateAuthor(author: Author): Future[Option[Author]]
  def deleteAuthor(id: UUID): Future[Option[Author]]
}
