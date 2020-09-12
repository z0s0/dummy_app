package ru.otus.sc.book.model
import java.util.UUID

case class DeleteBookRequest(id: Option[UUID])

sealed trait DeleteBookResponse

case object DeleteBookResponse {
  case class Deleted(book: Book)     extends DeleteBookResponse
  case class NotFound(id: UUID)      extends DeleteBookResponse
  case object CantBeDeletedWithoutID extends DeleteBookResponse
}
