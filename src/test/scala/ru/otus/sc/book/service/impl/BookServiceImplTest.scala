package ru.otus.sc.book.service.impl

import java.util.UUID
import java.util.concurrent.ForkJoinPool

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.book.model.{
  Book,
  CreateBookRequest,
  CreateBookResponse,
  DeleteBookRequest,
  DeleteBookResponse,
  GetBookRequest,
  GetBookResponse,
  ListBooksResponse,
  UpdateBookRequest,
  UpdateBookResponse
}
import ru.otus.sc.author.model.Genre
import ru.otus.sc.book.dao.BookDao

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class BookServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {

  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new ForkJoinPool())

  val book1: Book = Book(
    id = Some(UUID.randomUUID()),
    authorName = "Rowling",
    name = "HP",
    pagesCount = 1000,
    publishedYear = 1999,
    genre = Genre.Programming
  )

  val book2: Book = Book(
    id = Some(UUID.randomUUID()),
    authorName = "Ham and gway",
    name = "Old man and sea",
    pagesCount = 100,
    publishedYear = 1950,
    genre = Genre.Programming
  )

  "getBook" - {
    "when book is unknown" in {
      val id      = UUID.randomUUID()
      val dao     = mock[BookDao]
      val srv     = new BookServiceImpl(dao, ThreadPool)
      val request = GetBookRequest(id)

      (dao.getBook _).expects(id).returns(Future.successful(None))
      srv.getBook(request).futureValue shouldBe GetBookResponse.NotFound
    }

    "when book is known" in {
      val id      = UUID.randomUUID()
      val dao     = mock[BookDao]
      val srv     = new BookServiceImpl(dao, ThreadPool)
      val request = GetBookRequest(id)

      (dao.getBook _).expects(id).returns(Future.successful(Some(book1)))
      srv.getBook(request).futureValue shouldBe GetBookResponse.Found(book1)
    }
  }

  "listBooks" in {
    val dao = mock[BookDao]
    val srv = new BookServiceImpl(dao, ThreadPool)

    (dao.listBooks _)
      .expects()
      .returns(Future.successful(List(book1, book2)))

    srv.listBooks.futureValue.books.toSet shouldBe Set(book2, book1)
  }

  "createBook" in {
    val dao = mock[BookDao]
    val srv = new BookServiceImpl(dao, ThreadPool)

    val request = CreateBookRequest(book1)

    (dao.createBook _).expects(*).returns(Future.successful(Some(book1)))

    srv.createBook(request).futureValue shouldBe CreateBookResponse.Created(book1)
  }

  "updateBook" - {
    "when known book" in {
      val dao = mock[BookDao]
      val srv = new BookServiceImpl(dao, ThreadPool)

      val request = UpdateBookRequest(book1)
      val newName = "Harry popkin"

      (dao.updateBook _).expects(book1).returns(Future.successful(Some(book1.copy(name = newName))))

      val response = srv.updateBook(request).futureValue
      response shouldBe UpdateBookResponse.Updated(book1.copy(name = newName))
    }

    "when unknown book" in {
      val dao = mock[BookDao]
      val srv = new BookServiceImpl(dao, ThreadPool)

      val request = UpdateBookRequest(book1)

      (dao.updateBook _).expects(book1).returns(Future.successful(None))

      srv.updateBook(request).futureValue shouldBe UpdateBookResponse.NotFound
    }
  }

  "deleteBook" - {
    "when known book" in {
      val dao = mock[BookDao]
      val srv = new BookServiceImpl(dao, ThreadPool)

      val request = DeleteBookRequest(book1.id)

      (dao.deleteBook _).expects(book1.id.get).returns(Future.successful(Some(book1)))
      srv.deleteBook(request).futureValue shouldBe DeleteBookResponse.Deleted(book1)
    }

    "when unknown book" in {
      val dao = mock[BookDao]
      val srv = new BookServiceImpl(dao, ThreadPool)

      val request = DeleteBookRequest(book1.id)

      (dao.deleteBook _).expects(book1.id.get).returns(Future.successful(None))
      srv.deleteBook(request).futureValue shouldBe (DeleteBookResponse.NotFound)
    }
  }
}
