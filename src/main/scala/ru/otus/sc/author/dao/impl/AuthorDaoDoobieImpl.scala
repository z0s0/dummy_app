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
import ru.otus.sc.author.dao.impl.AuthorDaoDoobieImpl.{AuthorGenre, AuthorRow}
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

  case class AuthorGenre(authorID: UUID, genreID: UUID)
}
class AuthorDaoDoobieImpl(tr: Transactor[IO]) extends AuthorDao {
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
      u <- selectAuthor(id)
    } yield u.map(_.toAuthor))
      .transact(tr)
      .unsafeToFuture()
  }

  private def selectAuthor(id: UUID): ConnectionIO[Option[AuthorRow]] = {
    sql"""
         select a.id, a.name, array_agg(g.name) from authors a
         left join authors_genres ag on ag.author_id = a.id
         left join genres g on g.id = ag.genre_id
         where a.id = $id
         group by 1,2
      """
      .query[AuthorRow]
      .option
  }

  private def insertGenres(authorID: UUID, genres: Set[Genre]): ConnectionIO[Int] = {
    NonEmptyList.fromList(genres.map(stringFromGenre).toList) match {
      case Some(listGenres) =>
        val genresIdsToInsert =
          (fr"select id from genres where" ++ Fragments.in(fr"name", listGenres))
            .query[UUID]
            .to[Vector]

        val sql =
          "insert into authors_genres(author_id, genre_id, created_at, updated_at) values (?, ?, NOW(), NOW())"

        genresIdsToInsert.flatMap { ids =>
          Update[AuthorGenre](sql).updateMany(ids.map(AuthorGenre(authorID, _)))
        }
      case None => 0.pure[ConnectionIO]
    }
  }

  override def createAuthor(author: Author): Future[Option[Author]] = {
    val insertAuthor =
      sql"""insert into authors(name, created_at, updated_at)
            values(${author.name}, NOW(), NOW()) 
         """.update
        .withGeneratedKeys[UUID]("id")
        .compile
        .lastOrError

    val res = for {
      newId <- insertAuthor
      _     <- insertGenres(newId, author.genres)
    } yield Some(author.copy(id = Some(newId)))

    res.transact(tr).unsafeToFuture()
  }

  override def updateAuthor(author: Author): Future[Option[Author]] = {
    author match {
      case Author(Some(id), name, genres) =>
        val update =
          sql"""
               update authors set name = $name
               where id = $id
             """.update.run

        val deleteGenres   = sql"""delete from authors_genres where author_id = $id""".update.run
        val insertGenresIO = insertGenres(id, genres)

        val res = for {
          authorRow <- selectAuthor(id)
          _ <- authorRow match {
            case Some(_) =>
              update *> deleteGenres *> insertGenresIO
            case None => ().pure[ConnectionIO]
          }
        } yield authorRow.map(_.toAuthor)

        res.transact(tr).unsafeToFuture()
      case _ => Future.successful(None)
    }
  }

  override def deleteAuthor(id: UUID): Future[Option[Author]] = {
    val res = for {
      authorRow <- selectAuthor(id)
      _ <- authorRow match {
        case Some(AuthorRow(_, _, _)) =>
          val deleteGenres = sql"""delete from authors_genres where author_id = $id""".update.run
          val deleteAuthor = sql"""delete from authors where id = $id""".update.run

          deleteGenres *> deleteAuthor
        case None => ().pure[ConnectionIO]

      }
    } yield authorRow.map(_.toAuthor)

    res.transact(tr).unsafeToFuture()
  }

  def deleteAll(): Future[Int] = {
    (sql"delete from authors_genres".update.run *> sql"delete from authors".update.run)
      .transact(tr)
      .unsafeToFuture()
  }
}
