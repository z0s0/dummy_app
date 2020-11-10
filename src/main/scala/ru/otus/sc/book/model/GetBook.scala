package ru.otus.sc.book.model

import java.util.UUID

import scala.concurrent.Future

case class GetBookRequest(id: UUID)

sealed trait GetBookResponse

case object GetBookResponse {

  case class Found(book: Book) extends GetBookResponse

  case object NotFound extends GetBookResponse

}

case class ListBooksResponse(books: Future[Seq[Book]])
