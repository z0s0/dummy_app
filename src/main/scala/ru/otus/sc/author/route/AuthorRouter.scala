package ru.otus.sc.author.route

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.author.model.{
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  GetAuthorRequest,
  GetAuthorResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}
import sttp.tapir.server.akkahttp._

import scala.concurrent.ExecutionContext

class AuthorRouter(
    authorService: AuthorService
)(implicit val threadPool: ExecutionContext) {
  def route: Route = getAuthor ~ createAuthor ~ updateAuthor ~ deleteAuthor ~ listAuthors

  def getAuthor: Route =
    AuthorRoutesDocs.getAuthor
      .toRoute(id => {
        val req = GetAuthorRequest(id)
        authorService.getAuthor(req).map {
          case GetAuthorResponse.Found(author) => Right(author)
          case GetAuthorResponse.NotFound      => Left(())
        }
      })

  private def listAuthors: Route =
    AuthorRoutesDocs.listAuthors
      .toRoute(_ => {
        authorService.listAuthors.map(res => Right(res.authors))
      })

  private def createAuthor: Route =
    AuthorRoutesDocs.createAuthor
      .toRoute(author => {
        authorService.createAuthor(CreateAuthorRequest(author)).map {
          case CreateAuthorResponse.Created(author) =>
            println(author)
            Right(author)
          case CreateAuthorResponse.Invalid => Left()
        }
      })

  private def updateAuthor: Route =
    AuthorRoutesDocs.updateAuthor
      .toRoute {
        case (id, author) =>
          val authorWithID = author.copy(id = Some(id))
          val req          = UpdateAuthorRequest(authorWithID)

          authorService.updateAuthor(req).map {
            case UpdateAuthorResponse.Updated(author) => Right(author)
            case UpdateAuthorResponse.Invalid         => Left()
          }
      }

  private def deleteAuthor: Route =
    AuthorRoutesDocs.deleteAuthor.toRoute(id => {
      authorService.deleteAuthor(DeleteAuthorRequest(id)).map {
        case DeleteAuthorResponse.Deleted(author) => Right(author)
        case DeleteAuthorResponse.NotFound        => Left()
      }
    })

}
