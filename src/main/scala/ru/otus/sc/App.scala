package ru.otus.sc

import ru.otus.sc.author.model.{
  Author,
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  GetAuthorRequest,
  GetAuthorResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}
import ru.otus.sc.book.model.Book
import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.author.service.impl.AuthorServiceImpl
import ru.otus.sc.book.service.BookService
import ru.otus.sc.book.service.impl.BookServiceImpl
import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl
import ru.otus.sc.book.dao.impl.BookDaoImpl
import ru.otus.sc.book.model.{
  CreateBookRequest,
  CreateBookResponse,
  DeleteBookRequest,
  DeleteBookResponse,
  GetBookRequest,
  GetBookResponse,
  UpdateBookRequest,
  UpdateBookResponse
}
import ru.otus.sc.filters.model.{FilterAuthorsRequest, FilterBooksRequest}
import ru.otus.sc.filters.service.FilterService
import ru.otus.sc.filters.service.impl.FilterServiceImpl

trait App {
  def listAuthors(): Seq[Author]
  def getAuthor(request: GetAuthorRequest): GetAuthorResponse
  def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse
  def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse
  def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse

  def listBooks(): Seq[Book]
  def getBook(request: GetBookRequest): GetBookResponse
  def createBook(request: CreateBookRequest): CreateBookResponse
  def updateBook(request: UpdateBookRequest): UpdateBookResponse
  def deleteBook(request: DeleteBookRequest): DeleteBookResponse

  def filterBooks(request: FilterBooksRequest): Seq[Book]
  def filterAuthors(request: FilterAuthorsRequest): Seq[Author]
}

object App {
  private class AppImpl(
      authorService: AuthorService,
      bookService: BookService,
      filterService: FilterService
  ) extends App {

    def getBook(request: GetBookRequest): GetBookResponse = bookService.get(request)
    def listBooks(): Seq[Book]                            = bookService.listBooks()

    def createBook(request: CreateBookRequest): CreateBookResponse =
      bookService.createBook(request)

    def deleteBook(request: DeleteBookRequest): DeleteBookResponse =
      bookService.deleteBook(request)

    def updateBook(request: UpdateBookRequest): UpdateBookResponse =
      bookService.updateBook(request)

    def listAuthors(): Seq[Author] = authorService.list()

    def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse =
      authorService.updateAuthor(request)

    def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse =
      authorService.createAuthor(request)

    def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse =
      authorService.deleteAuthor(request)

    def getAuthor(request: GetAuthorRequest): GetAuthorResponse = authorService.get(request)

    def filterBooks(request: FilterBooksRequest): Seq[Book] = filterService.booksBy(request)

    def filterAuthors(request: FilterAuthorsRequest): Seq[Author] = filterService.authorsBy(request)
  }

  def apply(): App = {
    val authorDao     = new AuthorDaoMapImpl
    val bookDao       = new BookDaoImpl
    val authorService = new AuthorServiceImpl(authorDao)
    val bookService   = new BookServiceImpl(dao = bookDao)
    val filterService =
      new FilterServiceImpl(authorService = authorService, bookService = bookService)

    new AppImpl(authorService, bookService, filterService)
  }
}
