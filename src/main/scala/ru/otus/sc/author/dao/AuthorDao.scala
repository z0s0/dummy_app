package ru.otus.sc.author.dao

import java.util.UUID

import ru.otus.sc.author.model.Author

trait AuthorDao {
  def listAuthors(): Seq[Author]
  def get(id: UUID): Option[Author]
  def createAuthor(author: Author): Author
  def updateAuthor(author: Author): Option[Author]
  def deleteAuthor(id: UUID): Option[Author]
}
