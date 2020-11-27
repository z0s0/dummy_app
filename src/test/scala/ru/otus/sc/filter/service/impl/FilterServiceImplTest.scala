package ru.otus.sc.filter.service.impl

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl
import ru.otus.sc.author.model.{Author, Genre}
import ru.otus.sc.author.service.impl.AuthorServiceImpl
import ru.otus.sc.book.dao.impl.BookDaoMapImpl
import ru.otus.sc.book.model.Book
import ru.otus.sc.book.service.impl.BookServiceImpl
import ru.otus.sc.ThreadPool.CustomThreadPool
import ru.otus.sc.filter.model.{
  AuthorsFilter,
  BooksFilter,
  FilterAuthorsRequest,
  FilterBooksRequest
}

import scala.concurrent.Await
import scala.concurrent.duration._

class FilterServiceImplTest extends AnyFreeSpec with ScalaFutures with MockFactory {

  val bookDao       = new BookDaoMapImpl
  val authorDao     = new AuthorDaoMapImpl
  val bookService   = new BookServiceImpl(bookDao)
  val authorService = new AuthorServiceImpl(authorDao)

  val book1: Book = Book(
    id = Some(UUID.randomUUID()),
    authorName = "Serega",
    name = "HP",
    pagesCount = 1000,
    publishedYear = 1999,
    genre = Genre.NoGenre
  )

  val book2: Book = Book(
    id = Some(UUID.randomUUID()),
    authorName = "Vladimir",
    name = "Old man and sea",
    pagesCount = 100,
    publishedYear = 1950,
    genre = Genre.NoGenre
  )

  val book3: Book = Book(
    id = Some(UUID.randomUUID()),
    authorName = "Viktor",
    name = "Chapaev",
    pagesCount = 5,
    publishedYear = 1986,
    genre = Genre.NoGenre
  )

  val author1: Author = Author(
    id = Some(UUID.randomUUID()),
    name = "Serega"
  )

  val author2: Author = Author(
    id = Some(UUID.randomUUID()),
    name = "Vladimir"
  )

  val author3: Author = Author(
    id = Some(UUID.randomUUID()),
    name = "Viktor"
  )

  private def createBook(book: Book) = Await.result(bookDao.createBook(book), 1.second)

  private def createAuthor(author: Author) = Await.result(authorDao.createAuthor(author), 1.second)

  private def setupAll() = {
    Seq(author1, author2, author1).foreach(createAuthor)
    Seq(book1, book2, book3).foreach(createBook)
  }

  "filterAuthors" - {
    "when empty filter and empty limit" in {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterAuthorsRequest(filters = None, limit = None)

      Seq(author1, author2).foreach(createAuthor)

      srv.filterAuthors(req).futureValue.authors.toSet shouldBe Set(author1, author2)
    }

    "when empty filter and filled limit" in {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterAuthorsRequest(filters = None, limit = Some(1))

      Seq(author1, author2, author3).foreach(createAuthor)

      val result = srv.filterAuthors(req).futureValue.authors

      result.length shouldBe 1
      result should contain oneElementOf Seq(author1, author2, author3)
    }

    "when filled filter and empty limit" in {
      val srv    = new FilterServiceImpl(authorService, bookService)
      val filter = Seq(AuthorsFilter.ByPublicationYear(1950))
      val req    = FilterAuthorsRequest(limit = None, filters = Some(filter))

      setupAll()

      val result = srv.filterAuthors(req).futureValue.authors

      result.length shouldBe 1
      result.head shouldBe author2
    }
  }

  "filterBooks" - {
    "when empty filter and empty limit" in {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterBooksRequest(filters = None, limit = None)

      Seq(book1, book2).foreach(createBook)

      srv.filterBooks(req).futureValue.books.toSet shouldBe Set(book1, book2)
    }

    "when empty filter and filled limit" in {
      val srv = new FilterServiceImpl(authorService, bookService)
      val req = FilterBooksRequest(filters = None, limit = Some(1))

      val books = Seq(book1, book2).map(createBook).map(_.get)

      val result = srv.filterBooks(req).futureValue.books

      result.length shouldBe 1
      result should contain oneElementOf (books)
    }

    "when filled filter and empty limit" in {
      val srv    = new FilterServiceImpl(authorService, bookService)
      val filter = Seq(BooksFilter.ByName("HP"))
      val req    = FilterBooksRequest(filters = Some(filter), limit = None)

      Seq(book1, book2).foreach(createBook)

      srv.filterBooks(req).futureValue.books.toSet shouldBe Seq(book1).toSet
    }

    "when searching for significant authors" in {
      val srv    = new FilterServiceImpl(authorService, bookService)
      val filter = Seq(BooksFilter.WithSignificantAuthors)
      val req    = FilterBooksRequest(filters = Some(filter), limit = None)

      Seq(author1, author2, author3).foreach(createAuthor)
      Seq(book2, book1, book3).foreach(createBook)

      srv.filterBooks(req).futureValue.books.toSet shouldBe Set(book1, book2)
    }
  }
}
