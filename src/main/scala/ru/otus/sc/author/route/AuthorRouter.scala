package ru.otus.sc.author.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.author.service.AuthorService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.author.json.AuthorJsonProtocol._
import ru.otus.sc.author.model.{
  Author,
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  GetAuthorRequest,
  GetAuthorResponse
}

class AuthorRouter(authorService: AuthorService) {
  def route: Route =
    pathPrefix("authors") {
      getAuthor ~ listAuthors ~ createAuthor ~ deleteAuthor
    }

  private def getAuthor: Route =
    (get & path(JavaUUID.map(GetAuthorRequest))) { request =>
      onSuccess(authorService.getAuthor(request)) {
        case GetAuthorResponse.Found(author) => complete(author)
        case GetAuthorResponse.NotFound      => complete(StatusCodes.NotFound)
      }
    }

  private def listAuthors: Route =
    get {
      onSuccess(authorService.listAuthors) { response =>
        complete(response.authors)
      }
    }

  private def createAuthor: Route = {
    (post & entity(as[Author]).map(CreateAuthorRequest)) { request =>
      onSuccess(authorService.createAuthor(request)) {
        case CreateAuthorResponse.Created(author) =>
          complete(author)
        case CreateAuthorResponse.Invalid =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }
  }

  private def updateAuthor: Route = ???

  private def deleteAuthor: Route =
    (delete & path(JavaUUID.map(DeleteAuthorRequest))) { request =>
      onSuccess(authorService.deleteAuthor(request)) {
        case DeleteAuthorResponse.Deleted(author) =>
          complete(StatusCodes.Found, author)
        case DeleteAuthorResponse.NotFound =>
          complete(StatusCodes.NotFound)
      }
    }
}
