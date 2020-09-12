package ru.otus.sc.book.dao

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.author.model.Genre
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import ru.otus.sc.book.model.Book
import org.scalacheck.Prop.propBoolean

import scala.util.Random

abstract class BookDaoTest(name: String, createDao: () => BookDao)
    extends AnyFreeSpec
    with ScalaCheckDrivenPropertyChecks {

  implicit lazy val genGenre: Gen[Genre] =
    Gen.oneOf(Genre.Programming, Genre.Management, Genre.Horror, Genre.NoGenre)
  implicit lazy val arbitraryGenre: Arbitrary[Genre] = Arbitrary(genGenre)

  implicit lazy val posInt: Gen[Int] = arbitrary[Int] suchThat { i => i > 0 }

  implicit lazy val genBook: Gen[Book] = for {
    id            <- Gen.uuid
    bookName      <- arbitrary[String]
    authorName    <- arbitrary[String]
    genre         <- arbitrary[Genre]
    publishedYear <- arbitrary[Int]
    pagesCount    <- arbitrary[Int]
  } yield Book(
    id = id,
    name = bookName,
    authorName = authorName,
    genre = genre,
    publishedYear = publishedYear,
    pagesCount = pagesCount
  )

  implicit lazy val arbitraryBook: Arbitrary[Book] = Arbitrary(genBook)

  "getBook" - {
    "when unknown" in {
      forAll { (books: Seq[Book], book: Book) =>
        val dao = createDao()
        books.foreach(dao.createBook)

        dao.getBook(book.id) shouldBe None
      }
    }
    "when book exists" in {
      forAll { books: Seq[Book] =>
        (books.nonEmpty) ==> {
          val dao          = createDao()
          val createdBooks = books.map(dao.createBook)
          val randBook     = Random.shuffle(createdBooks).head

          dao.getBook(randBook.id) shouldBe randBook
          dao.getBook(randBook.id).isDefined
        }
      }
    }
  }

  "listBooks" in {
    forAll { books: Seq[Book] =>
      val dao          = createDao()
      val createdBooks = books.map(dao.createBook)

      createdBooks.toSet shouldBe books.toSet
    }
  }

  "createBook" - {
    forAll { book: Book =>
      val dao         = createDao()
      val createdBook = dao.createBook(book)
      createdBook shouldNot be(None)
      dao.getBook(createdBook.id) shouldNot be(None)
    }
  }

  "updateBook" - {
    "existing book" in {
      forAll { books: Seq[Book] =>
        (books.nonEmpty) ==> {
          val dao          = createDao()
          val createdBooks = books.map(dao.createBook)
          val newBookName  = "Harry pointer"
          val randBook     = Random.shuffle(createdBooks).head

          val updBook = randBook.copy(name = newBookName)

          dao.updateBook(updBook) shouldNot be(None)

          dao.getBook(updBook.id).get.name == newBookName
        }
      }
    }

    "unknown book" in {
      forAll { (books: Seq[Book], book: Book) =>
        val dao         = createDao()
        val newBookName = "Harry Pttr"
        books.foreach(dao.createBook)

        val updBook = book.copy(name = newBookName)

        dao.updateBook(updBook) shouldBe None
      }
    }
  }

}
