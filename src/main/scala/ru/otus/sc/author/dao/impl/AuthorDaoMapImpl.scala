package ru.otus.sc.author.dao.impl

import java.util.UUID

import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model.Author

class AuthorDaoMapImpl extends AuthorDao {
  private var authors: Map[UUID, Author] = Map.empty

  def get(id: UUID): Option[Author] = authors.get(id)

  def listAuthors(): List[Author] = authors.values.toList

  def createAuthor(author: Author): Author = {
    authors += (author.id -> author)
    author
  }

  def updateAuthor(author: Author): Option[Author] = {
    authors.get(author.id) match {
      case Some(_) =>
        authors += (author.id -> author)
        Some(author)
      case None =>
        None
    }
  }

  def deleteAuthor(id: UUID): Option[Author] = {
    authors.get(id) match {
      case None => None

      case Some(author) =>
        authors -= author.id
        Some(author)
    }
  }
}
