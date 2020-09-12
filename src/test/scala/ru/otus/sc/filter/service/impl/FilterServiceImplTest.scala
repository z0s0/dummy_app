package ru.otus.sc.filter.service.impl

import java.util.UUID

import org.scalatest.freespec.AnyFreeSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.book.dao.impl.BookDaoImpl
import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl
import ru.otus.sc.book.service.impl.BookServiceImpl
import ru.otus.sc.author.service.impl.AuthorServiceImpl
import ru.otus.sc.book.model.{Book, CreateBookRequest}
import ru.otus.sc.filters.service.impl.FilterServiceImpl
import ru.otus.sc.filters.model.{
  AuthorsFilter,
  BooksFilter,
  FilterAuthorsRequest,
  FilterBooksRequest
}
import ru.otus.sc.author.model.Author

class FilterServiceImplTest extends AnyFreeSpec with MockFactory {
  val bookDao       = new BookDaoImpl
  val authorDao     = new AuthorDaoMapImpl
  val bookService   = new BookServiceImpl(bookDao)
  val authorService = new AuthorServiceImpl(authorDao)
  val book1: Book = Book(
    id = UUID.randomUUID(),
    authorName = "Serega",
    name = "HP",
    pagesCount = 1000,
    publishedYear = 1999
  )

  val book2: Book = Book(
    id = UUID.randomUUID(),
    authorName = "Vladimir",
    name = "Old man and sea",
    pagesCount = 100,
    publishedYear = 1950
  )

  val book3: Book = Book(
    id = UUID.randomUUID(),
    authorName = "Viktor",
    name = "Chapaev",
    pagesCount = 5,
    publishedYear = 1986
  )

  val author1: Author = Author(
    id = UUID.randomUUID(),
    name = "Serega"
  )

  val author2: Author = Author(
    id = UUID.randomUUID(),
    name = "Vladimir"
  )

  val author3: Author = Author(
    id = UUID.randomUUID(),
    name = "Viktor"
  )

  private def createBook(book: Book) = bookDao.createBook(book)

  private def createAuthor(author: Author) = authorDao.createAuthor(author)

  private def setupAll() = {
    Seq(author1, author2, author1).foreach(createAuthor)
    Seq(book1, book2, book3).foreach(createBook)
  }

  "booksBy" - {
    "when empty filter and empty limit" - {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterBooksRequest(filter = None, limit = None)

      val books = Seq(book1, book2).map(createBook)

      srv.booksBy(req).toSet shouldBe books.toSet
    }

    "when empty filter and filled limit" - {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterBooksRequest(filter = None, limit = Some(1))

      val books = Seq(book1, book2).map(createBook)

      val result = srv.booksBy(req)

      result.length shouldBe 1
      result should contain oneElementOf (books)
    }

    "when filled filter and empty limit" - {
      val srv    = new FilterServiceImpl(authorService, bookService)
      val filter = Seq(BooksFilter.ByName("HP"))
      val req    = FilterBooksRequest(filter = Some(filter), limit = None)

      Seq(book1, book2).foreach(createBook)

      srv.booksBy(req) shouldBe Seq(book1)
    }

    "when searching for significant authors" in {
      val srv    = new FilterServiceImpl(authorService, bookService)
      val filter = Seq(BooksFilter.WithSignificantAuthors)
      val req    = FilterBooksRequest(filter = Some(filter), limit = None)

      Seq(author1, author2, author3).foreach(createAuthor)
      Seq(book2, book1, book3).foreach(createBook)

      srv.booksBy(req).toSet shouldBe Set(book1, book2)
    }
  }

  "authorsBy" - {
    "when empty filter and empty limit" - {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterAuthorsRequest(filter = None, limit = None)

      Seq(author1, author2).foreach(createAuthor)

      srv.authorsBy(req).toSet shouldBe Set(author1, author2)
    }

    "when empty filter and filled limit" - {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterAuthorsRequest(filter = None, limit = Some(1))

      Seq(author1, author2, author3).foreach(createAuthor)

      val result = srv.authorsBy(req)

      result.length shouldBe 1
      result should contain oneElementOf Seq(author1, author2, author3)
    }

    "when filled filter and empty limit" - {
      val srv    = new FilterServiceImpl(authorService, bookService)
      val filter = Seq(AuthorsFilter.ByPublicationYear(1950))
      val req    = FilterAuthorsRequest(limit = None, filter = Some(filter))

      setupAll()

      val result = srv.authorsBy(req)

      result.length shouldBe 1
      result.head shouldBe author2
    }
  }
}
