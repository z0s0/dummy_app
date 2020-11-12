package ru.otus.sc.author.dao

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import ru.otus.sc.author.model.{Author, Genre}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Prop.propBoolean
import Arbitrary.arbitrary
import org.scalatest.concurrent.ScalaFutures

import scala.util.Random

abstract class AuthorDaoTest(
    name: String,
    createDao: () => AuthorDao
) extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks
    with ScalaFutures {

  implicit lazy val genGenre: Gen[Genre]             = Gen.oneOf(Genre.Horror, Genre.Programming, Genre.NoGenre)
  implicit lazy val arbitraryGenre: Arbitrary[Genre] = Arbitrary(genGenre)

  implicit lazy val genAuthor: Gen[Author] = for {
    id    <- Gen.uuid
    name  <- arbitrary[String]
    genre <- arbitrary[Genre]
  } yield Author(
    id = Some(id),
    name = name,
    genres = Set(genre)
  )

  implicit lazy val arbitraryAuthor: Arbitrary[Author] = Arbitrary(genAuthor)

  "listAuthors" - {
    "when no authors present" in {
      val dao = createDao()
      dao.listAuthors.futureValue shouldBe List[Author]()
    }

    "when authors present" in {
      forAll { authors: Seq[Author] =>
        val dao = createDao()
        val createdAuthors: Future[Seq[Option[Author]]] =
          Future.sequence(authors.map(dao.createAuthor))
        createdAuthors.futureValue.map(_.get.name).toSet shouldBe authors.map(_.name).toSet
      }
    }
  }

  "getAuthor" - {
    "when unknown author" in {
      forAll { (authors: Seq[Author], author: Author) =>
        val dao = createDao()
        authors.foreach(dao.createAuthor)

        dao.getAuthor(author.id.get).futureValue shouldBe None
      }
    }

    "when known author" in {
      forAll { (authors: Seq[Author]) =>
        authors.nonEmpty ==> {
          val dao = createDao()
          authors.foreach(dao.createAuthor)
          val randAuthor = Random.shuffle(authors).head

          dao.getAuthor(randAuthor.id.get).futureValue shouldBe (12)
          throw new RuntimeException
          1 == 2
        }
      }
    }
  }

  "createAuthor" - {
    "creates new Author from arbitrary" in {
      forAll { author: Author =>
        val dao           = createDao()
        val createdAuthor = dao.createAuthor(author).futureValue.get

        dao.getAuthor(createdAuthor.id.get).futureValue.get shouldBe createdAuthor
      }
    }
  }

  "updateAuthor" - {
    "when unknown author" in {
      forAll { (authors: Seq[Author], author: Author) =>
        val dao = createDao()
        authors.foreach(dao.createAuthor)

        dao.updateAuthor(author).futureValue shouldBe None
      }
    }

    "when known author" in {
      forAll { authors: Seq[Author] =>
        (authors.nonEmpty) ==> {
          val dao = createDao()
          println(dao)
          println("BEFORE CREATES")
          val createdAuthors = Future.sequence(authors.map(dao.createAuthor)).futureValue.map(_.get)
          println(createdAuthors)

          val randAuthor    = Random.shuffle(createdAuthors).head
          val newAuthorName = "Vasily Seregin"

          val updatedAuthor =
            dao.updateAuthor(randAuthor.copy(name = newAuthorName)).futureValue.get

          println(updatedAuthor)
          dao.getAuthor(randAuthor.id.get).futureValue.get.name == "Pidor"
        }
      }
    }
  }

  "deleteAuthor" - {
    "when known author" in {
      forAll { authors: Seq[Author] =>
        authors.nonEmpty ==> {
          val dao = createDao()
          authors.foreach(dao.createAuthor)
          val randAuthor = Random.shuffle(authors).head

          dao.getAuthor(randAuthor.id.get).futureValue.contains(1)
        }
      }
    }
  }
}
