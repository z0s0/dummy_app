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
  GetAuthorResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}

class AuthorRouter(authorService: AuthorService) {
  def route: Route =
    pathPrefix("authors") {
      getAuthor ~ listAuthors ~ createAuthor ~ deleteAuthor ~ updateAuthor
    }

  private def getAuthor: Route =
    (get & path(JavaUUID.map(GetAuthorRequest))) { request =>
      onSuccess(authorService.getAuthor(request)) {
        case GetAuthorResponse.Found(author) => complete(author)
        case GetAuthorResponse.NotFound      => complete(StatusCodes.NotFound)
      }
    }

  private def listAuthors: Route = {
    get {
      onSuccess(authorService.listAuthors) { resp =>
        complete(resp.authors)
      }
    }
  }

  private def createAuthor: Route = {
    (post & entity(as[Author]).map(CreateAuthorRequest)) { request =>
      onSuccess(authorService.createAuthor(request)) {
        case CreateAuthorResponse.Created(author) =>
          complete(StatusCodes.Created, author)
        case CreateAuthorResponse.Invalid =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }
  }

  private def updateAuthor: Route = {
    (put & path(JavaUUID) & entity(as[Author])) { (id, author) =>
      val authorWithID = author.copy(id = Some(id))
      val request      = UpdateAuthorRequest(authorWithID)

      onSuccess(authorService.updateAuthor(request)) {
        case UpdateAuthorResponse.Updated(author) =>
          complete(author)
        case UpdateAuthorResponse.Invalid =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }
  }

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
