package ru.otus.sc.author.dao

import java.util.UUID

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.otus.sc.author.model.{Author, Genre}
import org.scalacheck.Prop.propBoolean

import scala.util.Random

abstract class AuthorDaoTest(
    name: String,
    createDao: () => AuthorDao
) extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks {
  implicit val genGenre: Gen[Genre]             = Gen.oneOf(Genre.Horror, Genre.Management, Genre.Programming)
  implicit val arbitraryGenre: Arbitrary[Genre] = Arbitrary(genGenre)

  implicit val genAuthor: Gen[Author] = for {
    id     <- Gen.uuid
    name   <- arbitrary[String]
    genres <- arbitrary[Seq[Genre]]
  } yield (Author(id = id, name = name, genres = genres.toSet))

  implicit val arbitraryAuthor: Arbitrary[Author] = Arbitrary(genAuthor)

  "get" - {
    "get unknown author" in {
      forAll { (authors: Seq[Author], userID: UUID) =>
        val dao = createDao()
        authors.foreach(dao.createAuthor)

        dao.get(userID) shouldBe None
      }
    }

    "known author" in {
      forAll { authors: Seq[Author] =>
        (authors.nonEmpty) ==> {
          val dao = createDao()

          val randAuthor = Random.shuffle(authors).head
          val author     = dao.get(randAuthor.id)

          author shouldNot be(None)
          author.get.id == randAuthor.id
        }
      }
    }
  }

  "listAuthors" in {
    forAll { authors: Seq[Author] =>
      val dao            = createDao()
      val createdAuthors = authors.map(dao.createAuthor)

      dao.listAuthors().toSet shouldBe createdAuthors.toSet
    }
  }

  "createAuthor" - {
    "creates all valid authors" in {
      forAll { authors: Seq[Author] =>
        val dao = createDao()
        authors.foreach(dao.createAuthor)

        authors.length shouldBe dao.listAuthors().length
      }
    }
  }

  "updateAuthor" - {
    "None if unknown author" in {
      forAll { (authors: Seq[Author], author: Author) =>
        val dao = createDao()
        authors.foreach(dao.createAuthor)

        dao.updateAuthor(author) shouldBe None
      }
    }

    "updates existing author" in {
      forAll { (authors: Seq[Author]) =>
        (authors.nonEmpty) ==> {
          val dao            = createDao()
          val createdAuthors = authors.map(dao.createAuthor)
          val randAuthor     = Random.shuffle(createdAuthors).head
          val newName        = "Serega128"

          val updRandAuthor = randAuthor.copy(name = newName)

          dao.updateAuthor(updRandAuthor)

          dao.get(randAuthor.id).get.name == newName
        }
      }
    }
  }

  "deleteAuthor" - {
    "when unknown author" in {
      forAll { (authors: Seq[Author], author: Author) =>
        val dao            = createDao()
        val createdAuthors = authors.map(dao.createAuthor)

        dao.deleteAuthor(author.id) shouldBe None
        createdAuthors.length shouldBe authors.length
      }
    }

    "when known author" in {
      forAll { authors: Seq[Author] =>
        (authors.nonEmpty) ==> {
          val dao            = createDao()
          val createdAuthors = authors.map(dao.createAuthor)
          val randAuthor     = Random.shuffle(createdAuthors).head

          dao.deleteAuthor(randAuthor.id) shouldBe randAuthor

          dao.get(randAuthor.id).isEmpty
        }
      }
    }
  }
}
