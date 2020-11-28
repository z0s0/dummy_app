package ru.otus.sc.author.dao

import java.util.UUID

import ru.otus.sc.author.model.Author
import ru.otus.sc.support.Generators._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._

abstract class AuthorDaoTest(
    name: String
) extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks
    with ScalaFutures {

  def createEmptyDao: AuthorDao

  "listAuthors" - {
    "when no authors present" in {
      val dao = createEmptyDao
      dao.listAuthors.futureValue shouldBe List[Author]()
    }

    "when authors present" in {
      forAll { authors: Seq[Author] =>
        val dao = createEmptyDao
        val createdAuthors: Future[Seq[Option[Author]]] =
          Future.sequence(authors.map(dao.createAuthor))
        createdAuthors.futureValue.map(_.get.name).toSet shouldBe authors.map(_.name).toSet
      }
    }
  }

  "getAuthor" - {
    "when unknown author" in {
      forAll { (authors: Seq[Author], author: Author) =>
        val dao = createEmptyDao
        Future.sequence(authors.map(dao.createAuthor)).futureValue

        dao.getAuthor(author.id.get).futureValue shouldBe None
      }
    }

    "when known author" in {
      val dao           = createEmptyDao
      val author        = genAuthor.sample.get
      val createdAuthor = dao.createAuthor(author).futureValue.get

      dao.getAuthor(createdAuthor.id.get).futureValue.get shouldBe author.copy(id =
        createdAuthor.id
      )
    }
  }

  "createAuthor" - {
    "creates new Author from arbitrary" in {
      forAll { author: Author =>
        val dao           = createEmptyDao
        val createdAuthor = dao.createAuthor(author).futureValue.get

        dao.getAuthor(createdAuthor.id.get).futureValue.get shouldBe createdAuthor
      }
    }
  }

  "updateAuthor" - {
    "when unknown author" in {
      val dao    = createEmptyDao
      val author = genAuthor.sample.get

      dao.updateAuthor(author).futureValue shouldBe None
    }

    "when known author" in {
      val dao    = createEmptyDao
      val author = genAuthor.sample.get

      val newName       = "vasily"
      val createdAuthor = Await.result(dao.createAuthor(author), 2.seconds).get

      Await.result(dao.updateAuthor(createdAuthor.copy(name = newName)), 2.seconds)

      Await.result(dao.getAuthor(createdAuthor.id.get), 2.seconds).get.name shouldBe newName
    }
  }

  "deleteAuthor" - {
    "when known author" in {
      val dao    = createEmptyDao
      val author = genAuthor.sample.get

      val createdAuthor = dao.createAuthor(author).futureValue.get

      val deletedAuthor = dao.deleteAuthor(createdAuthor.id.get).futureValue.get

      deletedAuthor shouldBe createdAuthor

      dao.listAuthors.futureValue shouldBe Vector[Author]()
    }

    "when unknown author" in {
      val dao = createEmptyDao

      dao.deleteAuthor(UUID.randomUUID()).futureValue shouldBe None
    }
  }
}
