package ru.otus.sc.book.model

case class CreateBookRequest(book: Book)

sealed trait CreateBookResponse

case object CreateBookResponse {

  case class Created(book: Book) extends CreateBookResponse

  case object Invalid extends CreateBookResponse

}
