package ru.otus.sc.book.dao

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import ru.otus.sc.support.Generators._

abstract class BookDaoTest(name: String, createDao: () => BookDao)
    extends AnyFreeSpec
    with ScalaFutures
    with ScalaCheckDrivenPropertyChecks {

  "getBook" - {
    "when known book" in {
      val dao  = createDao()
      val book = genBook.sample.get

      dao.createBook(book).futureValue

      dao.getBook(book.id.get).futureValue shouldBe Some(book)
    }

    "when unknown book" in {
      val dao = createDao()
      dao.getBook(UUID.randomUUID()).futureValue shouldBe None
    }
  }

  "createBook" in {
    val dao  = createDao()
    val book = genBook.sample.get

    val createdBook = dao.createBook(book).futureValue.get

    createdBook shouldBe book
    dao.getBook(book.id.get).futureValue shouldBe Some(book)
  }

  "updateBook" - {
    "when known book" in {
      val dao     = createDao()
      val book    = genBook.sample.get
      val newName = "??? Booooook"

      dao.createBook(book).futureValue.get
      dao.updateBook(book.copy(name = newName)).futureValue

      dao.getBook(book.id.get).futureValue.get.name shouldBe newName
    }

    "when unknown book" in {
      val dao  = createDao()
      val book = genBook.sample.get

      dao.updateBook(book).futureValue shouldBe None
    }
  }

  "deleteBook" - {
    "known book" in {
      val dao  = createDao()
      val book = genBook.sample.get

      dao.createBook(book).futureValue.get
      val deletedBook = dao.deleteBook(book.id.get).futureValue

      deletedBook shouldBe Some(book)
      dao.getBook(book.id.get).futureValue shouldBe None
    }

    "unknown book" in {
      val dao  = createDao()
      val book = genBook.sample.get

      val deletedBook = dao.deleteBook(book.id.get).futureValue

      deletedBook shouldBe None
      dao.getBook(book.id.get).futureValue shouldBe None
    }
  }
}
