package ru.otus.sc.book.route

import java.util.UUID

import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.endpoint
import sttp.tapir.json.play._
import sttp.tapir.generic.auto._
import ru.otus.sc.book.json.BookJsonProtocol._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.book.model.Book
import sttp.model.StatusCode

object BookRoutesDocs {
  val listBooks = endpoint.get
    .in("books")
    .out(jsonBody[Vector[Book]])

  val getBook = endpoint.get
    .in("books" / path[UUID])
    .out(jsonBody[Book])
    .errorOut(statusCode(StatusCode.NotFound))

  val createBook = endpoint.post
    .in("books")
    .in(jsonBody[Book])
    .out(jsonBody[Book])
    .out(statusCode(StatusCode.Created))
    .errorOut(statusCode(StatusCode.UnprocessableEntity))

  val updateBook = endpoint.put
    .in("books" / path[UUID])
    .in(jsonBody[Book])
    .out(jsonBody[Book])
    .errorOut(statusCode(StatusCode.NotFound))

  val deleteBook = endpoint.delete
    .in("books" / path[UUID])
    .out(jsonBody[Book])
    .out(statusCode(StatusCode.Found))
    .errorOut(statusCode(StatusCode.NotFound))

  val routes = List(listBooks, getBook, createBook, updateBook, deleteBook)

}
