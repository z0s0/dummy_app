package ru.otus.sc.author.dao.impl

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
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.dao.impl.AuthorDaoDoobieImpl.AuthorRow
import ru.otus.sc.author.model.{Author, Genre}
import ru.otus.sc.ModelHelpers._
import scala.concurrent.Future

object AuthorDaoDoobieImpl {
  case class AuthorRow(id: UUID, name: String, genres: Vector[Option[String]]) {
    def toAuthor: Author = {
      val convertedGenres = genres.filter(_.nonEmpty).map(g => genreFromString(g.get)).toSet

      Author(id = Some(id), name = name, genres = convertedGenres)
    }
  }
}
class AuthorDaoDoobieImpl(tr: Transactor[IO]) extends AuthorDao {
  def pidor(id: UUID) = {
    (for {
      u <- selectAuthor(id, forUpdate = false)
    } yield u.map(_.toAuthor))
      .transact(tr)
      .unsafeRunSync()
  }

  override def listAuthors: Future[Vector[Author]] =
    sql"""
         select a.id, a.name, array_agg(g.name) from authors a
         left join authors_genres ag on ag.author_id = a.id
         left join genres g on ag.genre_id = g.id
         group by 1,2
       """
      .query[AuthorRow]
      .map(_.toAuthor)
      .to[Vector]
      .transact(tr)
      .unsafeToFuture()

  override def getAuthor(id: UUID): Future[Option[Author]] = {
    (for {
      u <- selectAuthor(id, forUpdate = false)
    } yield u.map(_.toAuthor))
      .transact(tr)
      .unsafeToFuture()
  }

  private def selectAuthor(id: UUID, forUpdate: Boolean): ConnectionIO[Option[AuthorRow]] = {
    val base = fr"""
         select a.id, a.name, array_agg(g.name) from authors a
         left join authors_genres ag on ag.author_id = a.id
         left join genres g on g.id = ag.genre_id
         where a.id = $id
         group by 1,2
      """

    val sql = if (forUpdate) base ++ fr""" FOR UPDATE""" else base

    sql
      .query[AuthorRow]
      .option
  }

  override def createAuthor(author: Author): Future[Option[Author]] = ???

  override def updateAuthor(author: Author): Future[Option[Author]] = ???

  override def deleteAuthor(id: UUID): Future[Option[Author]] = ???
}
