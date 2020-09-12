package ru.otus.sc.book.service.impl

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import ru.otus.sc.book.dao.BookDao
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.book.model.{
  Book,
  CreateBookRequest,
  CreateBookResponse,
  DeleteBookRequest,
  DeleteBookResponse,
  GetBookRequest,
  GetBookResponse,
  UpdateBookRequest,
  UpdateBookResponse
}

class BookServiceImplTest extends AnyFreeSpec with MockFactory {
  val book1: Book = Book(
    id = UUID.randomUUID(),
    authorName = "Rowling",
    name = "HP",
    pagesCount = 1000,
    publishedYear = 1999
  )

  val book2: Book = Book(
    id = UUID.randomUUID(),
    authorName = "Ham and gway",
    name = "Old man and sea",
    pagesCount = 100,
    publishedYear = 1950
  )

  "BookServiceTest" - {
    "getBook" - {
      "request with unknown id" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getBook _).expects(id).returns(None)

        srv.get(GetBookRequest(id)) shouldBe GetBookResponse.NotFound(id)
      }

      "request with known id" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.getBook _).expects(id).returns(Some(book1))

        srv.get(GetBookRequest(id)) shouldBe GetBookResponse.Found(book1)
      }
    }

    "list" in {
      val dao = mock[BookDao]
      val srv = new BookServiceImpl(dao)

      (dao.listBooks: () => Seq[Book]).expects().returns(Seq(book1, book2))

      srv.listBooks().toSet shouldBe Seq(book1, book2).toSet
    }

    "createBook" - {
      "when valid params" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val request = CreateBookRequest(
          name = Some("Book"),
          authorName = Some("Vova"),
          pagesCount = Some(12),
          publishedYear = Some(10),
          genre = None
        )

        (dao.createBook _).expects(*).returns(book1)

        srv.createBook(request) shouldBe CreateBookResponse.Created(book1)
      }

      "when invalid params" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val request = CreateBookRequest(
          name = None,
          authorName = None,
          pagesCount = Some(12),
          publishedYear = Some(10),
          genre = None
        )

        srv.createBook(request) shouldBe CreateBookResponse.Invalid
      }
    }

    "updateBook" - {
      "when known id" in {
        val dao  = mock[BookDao]
        val srv  = new BookServiceImpl(dao)
        val id   = UUID.randomUUID()
        val book = book1.copy(id = id)

        (dao.updateBook _).expects(book).returns(Some(book))

        srv.updateBook(UpdateBookRequest(book)) shouldBe UpdateBookResponse.Updated(book)
      }

      "when unknown id" in {
        val dao  = mock[BookDao]
        val srv  = new BookServiceImpl(dao)
        val id   = UUID.randomUUID()
        val book = book1.copy(id = id)

        (dao.updateBook _).expects(book).returns(None)

        srv.updateBook(UpdateBookRequest(book)) shouldBe UpdateBookResponse.NotFound(id)
      }
    }

    "deleteBook" - {
      "when known id" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteBook _).expects(id).returns(Some(book1))

        srv.deleteBook(DeleteBookRequest(Some(id))) shouldBe DeleteBookResponse.Deleted(book1)
      }

      "when unknown id" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)
        val id  = UUID.randomUUID()

        (dao.deleteBook _).expects(id).returns(None)

        srv.deleteBook(DeleteBookRequest(Some(id))) shouldBe DeleteBookResponse.NotFound(id)
      }

      "when no id provided" in {
        val dao = mock[BookDao]
        val srv = new BookServiceImpl(dao)

        srv.deleteBook(DeleteBookRequest(None)) shouldBe DeleteBookResponse.CantBeDeletedWithoutID
      }
    }
  }
}
