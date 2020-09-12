package ru.otus.sc.book.model

import java.util.UUID

case class UpdateBookRequest(book: Book)

sealed trait UpdateBookResponse

case object UpdateBookResponse {
  case class Updated(book: Book) extends UpdateBookResponse
  case class NotFound(id: UUID)  extends UpdateBookResponse
}
