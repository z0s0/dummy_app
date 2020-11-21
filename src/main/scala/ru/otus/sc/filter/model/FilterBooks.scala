package ru.otus.sc.filter.model

import ru.otus.sc.book.model.Book

import ru.otus.sc.author.model.Genre

sealed trait BooksFilter

case object BooksFilter {
  case class ByName(name: String)         extends BooksFilter
  case class ByGenre(genre: Genre)        extends BooksFilter
  case class MinPagesCount(count: Int)    extends BooksFilter
  case class MaxPagesCount(maxCount: Int) extends BooksFilter
  case object WithSignificantAuthors      extends BooksFilter
}

case class FilterBooksRequest(filters: Option[Seq[BooksFilter]], limit: Option[Int])

case class FilterBooksResponse(books: Vector[Book])
