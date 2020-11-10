package ru.otus.sc.author.dao.impl

import java.util.UUID

import ru.otus.sc.ThreadPool.CustomThreadPool

import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model.Author

import scala.concurrent.Future

class AuthorDaoMapImpl extends AuthorDao {
  private var authors = Map[UUID, Author]()

  override def listAuthors: Future[Seq[Author]]            = Future(authors.values.toSeq)
  override def getAuthor(id: UUID): Future[Option[Author]] = Future(authors.get(id))

  override def createAuthor(author: Author): Future[Option[Author]] =
    Future {
      val id           = UUID.randomUUID()
      val authorWithID = author.copy(id = Some(id))

      if (isAuthorValid(authorWithID)) {
        authors += (id -> authorWithID)
        Some(authorWithID)
      } else None
    }

  override def updateAuthor(author: Author): Future[Option[Author]] =
    Future {
      if (isAuthorValid(author)) {
        for {
          id <- author.id
          _  <- authors.get(id)
        } yield {
          authors += (id -> author)
          author
        }
      } else None
    }

  override def deleteAuthor(id: UUID): Future[Option[Author]] =
    Future {
      authors.get(id) match {
        case a @ Some(_) =>
          authors -= id
          a

        case None => None
      }
    }

  private def isAuthorValid(author: Author): Boolean = true
}
