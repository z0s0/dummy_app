package ru.otus.sc.book.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.dao.impl.{AuthorDaoDoobieImpl, AuthorDaoDoobieImplTest}
import ru.otus.sc.author.model.Author
import ru.otus.sc.book.model.Book
import ru.otus.sc.support.Generators._

abstract class BookDaoTest(name: String)
    extends AnyFreeSpec
    with ScalaFutures
    with ScalaCheckDrivenPropertyChecks {

  def createDao: BookDao
  def createAuthorDao: AuthorDao

  "getBook" - {
    "when known book" in {
      val dao  = createDao
      val book = genBook.sample.get

      println(book)

      println(insertAuthorForBook(book))

      val createdBook = dao.createBook(book).futureValue.get

      println(createdBook)
      dao.getBook(createdBook.id.get).futureValue shouldBe Some(createdBook)
    }

    "when unknown book" in {
      val dao = createDao
      dao.getBook(UUID.randomUUID()).futureValue shouldBe None
    }
  }

  "createBook" in {
    val dao  = createDao
    val book = genBook.sample.get
    insertAuthorForBook(book)

    val createdBook = dao.createBook(book).futureValue.get

    createdBook shouldBe book.copy(id = createdBook.id)

    dao.getBook(createdBook.id.get).futureValue shouldBe Some(createdBook)
  }

  "updateBook" - {
    "when known book" in {
      val dao     = createDao
      val book    = genBook.sample.get
      val newName = "??? Booooook"

      insertAuthorForBook(book)

      val createdBook = dao.createBook(book).futureValue.get
      dao.updateBook(createdBook.copy(name = newName)).futureValue

      dao.getBook(createdBook.id.get).futureValue.get.name shouldBe newName
    }

    "when unknown book" in {
      val dao  = createDao
      val book = genBook.sample.get

      dao.updateBook(book).futureValue shouldBe None
    }
  }

  "deleteBook" - {
    "known book" in {
      val dao  = createDao
      val book = genBook.sample.get

      insertAuthorForBook(book)

      val createdBook = dao.createBook(book).futureValue.get
      val deletedBook = dao.deleteBook(createdBook.id.get).futureValue

      deletedBook shouldBe Some(createdBook)
      dao.getBook(createdBook.id.get).futureValue shouldBe None
    }

    "unknown book" in {
      val dao  = createDao
      val book = genBook.sample.get

      val deletedBook = dao.deleteBook(book.id.get).futureValue

      deletedBook shouldBe None
      dao.getBook(book.id.get).futureValue shouldBe None
    }
  }

  private def insertAuthorForBook(book: Book): Author = {
    val author = genAuthor.sample.get.copy(name = book.authorName)
    createAuthorDao.createAuthor(author).futureValue.get
  }

}
