package ru.otus.sc.filter.service.impl

import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.book.service.BookService
import ru.otus.sc.filter.model.{
  FilterAuthorsRequest,
  FilterAuthorsResponse,
  FilterBooksRequest,
  FilterBooksResponse
}
import ru.otus.sc.filter.service.FilterService

import scala.concurrent.Future

class FilterServiceImpl(authorService: AuthorService, bookService: BookService)
    extends FilterService {
  override def filterAuthors(request: FilterAuthorsRequest): Future[FilterAuthorsResponse] = ???
  override def filterBooks(request: FilterBooksRequest): Future[FilterBooksResponse]       = ???
}
