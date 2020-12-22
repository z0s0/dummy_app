package ru.otus.sc.author.route

import java.util.UUID

import ru.otus.sc.author.model.Author
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.{Endpoint, endpoint}
import sttp.tapir.json.play._
import sttp.tapir.server.akkahttp._
import ru.otus.sc.author.json.AuthorJsonProtocol._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import sttp.model.StatusCode

object AuthorRoutesDocs {

  val getAuthor = endpoint.get
    .in("authors" / path[UUID])
    .out(jsonBody[Author])
    .errorOut(statusCode(StatusCode.NotFound))

  val listAuthors = endpoint.get
    .in("authors")
    .out(jsonBody[Vector[Author]])

  val createAuthor = endpoint.post
    .in("authors")
    .in(header[Option[String]]("authorization"))
    .in(jsonBody[Author])
    .out(jsonBody[Author])
    .errorOut(statusCode(StatusCode.Unauthorized))
    .out(statusCode(StatusCode.Created))

  val updateAuthor = endpoint.put
    .in("authors" / path[UUID])
    .in(jsonBody[Author])
    .errorOut(statusCode(StatusCode.UnprocessableEntity))
    .out(jsonBody[Author])

  val deleteAuthor = endpoint.delete
    .in("authors" / path[UUID])
    .out(jsonBody[Author])
    .out(statusCode(StatusCode.Found))
    .errorOut(statusCode(StatusCode.NotFound))

  val routes = List(listAuthors, getAuthor, createAuthor, updateAuthor, deleteAuthor)
}
