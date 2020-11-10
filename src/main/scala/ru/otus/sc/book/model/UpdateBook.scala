package ru.otus.sc.book.model

case class UpdateBookRequest(book: Book)

sealed trait UpdateBookResponse

case object UpdateBookResponse {
  case class Updated(book: Book)    extends UpdateBookResponse
  case object NotFound              extends UpdateBookResponse
  case object CannotUpdateWithoutID extends UpdateBookResponse
}
