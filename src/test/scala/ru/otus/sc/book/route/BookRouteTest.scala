package ru.otus.sc.book.route

import java.util.UUID

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
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
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.book.json.BookJsonProtocol._
import ru.otus.sc.book.service.BookService
import ru.otus.sc.support.Generators.{arbitraryBook, genBook}

import scala.concurrent.Future

class BookRouteTest
    extends AnyFreeSpec
    with ScalatestRouteTest
    with MockFactory
    with ScalaFutures
    with ScalaCheckDrivenPropertyChecks {

  val srv: BookService = mock[BookService]
  val router           = new BookRouter(srv)

  "GET /books" in {
    val books = List(genBook.sample.get, genBook.sample.get)

    (srv.listBooks _).expects().returns(Future.successful(ListBooksResponse(books)))

    Get("/books") ~> router.route ~> check {
      handled shouldBe true
      status shouldBe StatusCodes.OK
    }
  }

  "GET /books/:uuid" - {
    "when unknown book" in {
      val randUUID = UUID.randomUUID()

      (srv.getBook _)
        .expects(GetBookRequest(randUUID))
        .returns(Future.successful(GetBookResponse.NotFound))

      Get(s"/books/$randUUID") ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
      }
    }

    "when known book" in {
      forAll { book: Book =>
        val request = Get(s"/books/${book.id.get}")

        (srv.getBook _)
          .expects(GetBookRequest(book.id.get))
          .returns(Future.successful(GetBookResponse.Found(book)))

        request ~> router.route ~> check {
          handled shouldBe true
          status shouldBe StatusCodes.OK
        }
      }
    }
  }

  "POST /books" in {
    forAll { book: Book =>
      val bookEntity = Marshal(book).to[MessageEntity].futureValue

      val request = Post("/books").withEntity(bookEntity)

      (srv.createBook _)
        .expects(CreateBookRequest(book))
        .returns(Future.successful(CreateBookResponse.Created(book)))

      request ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.Created
      }
    }
  }

  "PUT /books/:uuid" - {
    "when known book" in {
      forAll { book: Book =>
        (srv.updateBook _)
          .expects(UpdateBookRequest(book))
          .returns(Future(UpdateBookResponse.Updated(book)))

        val bookEntity = Marshal(book).to[MessageEntity].futureValue

        val request = Put(s"/books/${book.id.get}").withEntity(bookEntity)

        request ~> router.route ~> check {
          handled shouldBe true
          status shouldBe StatusCodes.OK
        }
      }
    }

    "when unknown book" in {
      val book = genBook.sample.get

      val bookEntity = Marshal(book).to[MessageEntity].futureValue

      (srv.updateBook _)
        .expects(UpdateBookRequest(book))
        .returns(Future.successful(UpdateBookResponse.NotFound))

      val request = Put(s"/books/${book.id.get}").withEntity(bookEntity)

      request ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
      }
    }
  }

  "DELETE /books/:uuid" - {
    "when known book" in {
      forAll { book: Book =>
        val request = Delete(s"/books/${book.id.get}")

        (srv.deleteBook _)
          .expects(DeleteBookRequest(book.id.get))
          .returns(Future.successful(DeleteBookResponse.Deleted(book)))

        request ~> router.route ~> check {
          handled shouldBe true
          status shouldBe StatusCodes.Found
        }
      }
    }

    "when unknown book" in {
      val randUUID = UUID.randomUUID()
      val request  = Delete(s"/books/${randUUID}")

      (srv.deleteBook _)
        .expects(DeleteBookRequest(randUUID))
        .returns(Future.successful(DeleteBookResponse.NotFound))

      request ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
