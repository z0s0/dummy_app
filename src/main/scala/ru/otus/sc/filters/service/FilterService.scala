package ru.otus.sc.filters.service

import ru.otus.sc.filters.model.{FilterAuthorsRequest, FilterBooksRequest}
import ru.otus.sc.book.model.Book
import ru.otus.sc.author.model.Author

object FilterService {
  val PublicationSizeToBeSignificant = 10
}

trait FilterService {
  def booksBy(request: FilterBooksRequest): Seq[Book]
  def authorsBy(request: FilterAuthorsRequest): Seq[Author]
}
