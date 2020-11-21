package ru.otus.sc.book.route

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.book.service.BookService
import ru.otus.sc.book.model.{
  CreateBookRequest,
  CreateBookResponse,
  DeleteBookRequest,
  DeleteBookResponse,
  GetBookRequest,
  GetBookResponse,
  UpdateBookRequest,
  UpdateBookResponse
}
import sttp.tapir.server.akkahttp._

import scala.concurrent.ExecutionContextExecutor

class BookRouter(bookService: BookService, implicit val ex: ExecutionContextExecutor) {
  def route: Route = getBook ~ listBooks ~ createBook ~ deleteBook ~ updateBook

  private def getBook: Route =
    BookRoutesDocs.getBook.toRoute(id => {
      bookService.getBook(GetBookRequest(id)).map {
        case GetBookResponse.Found(book) => Right(book)
        case GetBookResponse.NotFound    => Left()
      }
    })

  private def listBooks: Route =
    BookRoutesDocs.listBooks.toRoute(_ => bookService.listBooks.map(res => Right(res.books)))

  private def createBook: Route =
    BookRoutesDocs.createBook.toRoute(book => {
      bookService.createBook(CreateBookRequest(book)).map {
        case CreateBookResponse.Created(book) => Right(book)
        case CreateBookResponse.Invalid       => Left()
      }
    })

  private def updateBook: Route =
    BookRoutesDocs.updateBook.toRoute {
      case (id, book) =>
        val req = UpdateBookRequest(book.copy(id = Some(id)))

        bookService.updateBook(req).map {
          case UpdateBookResponse.Updated(book) => Right(book)
          case UpdateBookResponse.NotFound      => Left()
        }
    }

  private def deleteBook: Route =
    BookRoutesDocs.deleteBook.toRoute { id =>
      bookService.deleteBook(DeleteBookRequest(id)).map {
        case DeleteBookResponse.Deleted(book) => Right(book)
        case DeleteBookResponse.NotFound      => Left()
      }
    }
}
