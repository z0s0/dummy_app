package ru.otus.sc.book.model

import java.util.UUID

case class GetBookRequest(id: UUID)

sealed trait GetBookResponse

case object GetBookResponse {
  case class Found(book: Book)  extends GetBookResponse
  case class NotFound(id: UUID) extends GetBookResponse
}
