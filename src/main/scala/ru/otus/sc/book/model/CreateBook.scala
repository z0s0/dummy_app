package ru.otus.sc.book.model

import ru.otus.sc.author.model.Genre

case class CreateBookRequest(
    name: Option[String],
    authorName: Option[String],
    genre: Option[Genre],
    publishedYear: Option[Int],
    pagesCount: Option[Int]
)

sealed trait CreateBookResponse

case object CreateBookResponse {
  case class Created(book: Book) extends CreateBookResponse
  case object Invalid            extends CreateBookResponse
}
