package ru.otus.sc.book.model

import java.util.UUID

case class DeleteBookRequest(id: UUID)

sealed trait DeleteBookResponse

case object DeleteBookResponse {
  case class Deleted(book: Book) extends DeleteBookResponse
  case object NotFound           extends DeleteBookResponse
}
