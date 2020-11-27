package ru.otus.sc

import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl
import ru.otus.sc.author.model.{
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  GetAuthorRequest,
  GetAuthorResponse,
  ListAuthorsResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}
import ru.otus.sc.book.service.BookService
import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.author.service.impl.AuthorServiceImpl
import ru.otus.sc.book.dao.impl.BookDaoMapImpl
import ru.otus.sc.book.model.{
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
import ru.otus.sc.book.service.impl.BookServiceImpl
import ru.otus.sc.filter.model.{
  FilterAuthorsRequest,
  FilterAuthorsResponse,
  FilterBooksRequest,
  FilterBooksResponse
}
import ru.otus.sc.filter.service.FilterService
import ru.otus.sc.filter.service.impl.FilterServiceImpl

import scala.concurrent.Future

trait App {
  def getBook(request: GetBookRequest): Future[GetBookResponse]
  def listBooks: Future[ListBooksResponse]
  def createBook(request: CreateBookRequest): Future[CreateBookResponse]
  def updateBook(request: UpdateBookRequest): Future[UpdateBookResponse]
  def deleteBook(request: DeleteBookRequest): Future[DeleteBookResponse]

  def getAuthor(request: GetAuthorRequest): Future[GetAuthorResponse]
  def listAuthors: Future[ListAuthorsResponse]
  def createAuthor(request: CreateAuthorRequest): Future[CreateAuthorResponse]
  def updateAuthor(request: UpdateAuthorRequest): Future[UpdateAuthorResponse]
  def deleteAuthor(request: DeleteAuthorRequest): Future[DeleteAuthorResponse]

  def filterAuthors(request: FilterAuthorsRequest): Future[FilterAuthorsResponse]
  def filterBooks(request: FilterBooksRequest): Future[FilterBooksResponse]
}

object App {
  private class AppImpl(
      bookService: BookService,
      authorService: AuthorService,
      filterService: FilterService
  ) extends App {
    override def getBook(request: GetBookRequest): Future[GetBookResponse] =
      bookService.getBook(request)
    override def listBooks: Future[ListBooksResponse] = bookService.listBooks
    override def createBook(request: CreateBookRequest): Future[CreateBookResponse] =
      bookService.createBook(request)
    override def updateBook(request: UpdateBookRequest): Future[UpdateBookResponse] =
      bookService.updateBook(request)
    override def deleteBook(request: DeleteBookRequest): Future[DeleteBookResponse] =
      bookService.deleteBook(request)

    override def getAuthor(request: GetAuthorRequest): Future[GetAuthorResponse] =
      authorService.getAuthor(request)
    override def listAuthors: Future[ListAuthorsResponse] = authorService.listAuthors
    override def createAuthor(request: CreateAuthorRequest): Future[CreateAuthorResponse] =
      authorService.createAuthor(request)
    override def updateAuthor(request: UpdateAuthorRequest): Future[UpdateAuthorResponse] =
      authorService.updateAuthor(request)
    override def deleteAuthor(request: DeleteAuthorRequest): Future[DeleteAuthorResponse] =
      authorService.deleteAuthor(request)

    override def filterAuthors(request: FilterAuthorsRequest): Future[FilterAuthorsResponse] =
      filterService.filterAuthors(request)
    override def filterBooks(request: FilterBooksRequest): Future[FilterBooksResponse] =
      filterService.filterBooks(request)
  }

  def apply(): App = {
    import ru.otus.sc.ThreadPool.CustomThreadPool

    val bookDao       = new BookDaoMapImpl()
    val bookService   = new BookServiceImpl(bookDao)
    val authorDao     = new AuthorDaoMapImpl()
    val authorService = new AuthorServiceImpl(authorDao)
    val filterService =
      new FilterServiceImpl(authorService = authorService, bookService = bookService)

    new AppImpl(bookService, authorService, filterService)
  }
}
