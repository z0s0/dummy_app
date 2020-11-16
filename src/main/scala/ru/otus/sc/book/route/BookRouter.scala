package ru.otus.sc.book.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ru.otus.sc.book.service.BookService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.otus.sc.book.json.BookJsonProtocol._
import ru.otus.sc.book.model.{
  Book,
  CreateBookRequest,
  CreateBookResponse,
  DeleteBookRequest,
  DeleteBookResponse,
  GetBookRequest,
  GetBookResponse,
  UpdateBookRequest,
  UpdateBookResponse
}

class BookRouter(bookService: BookService) {
  def route: Route =
    pathPrefix("books") {
      getBook ~ listBooks ~ createBook ~ deleteBook ~ updateBook
    }

  private def getBook: Route =
    (get & path(JavaUUID.map(GetBookRequest))) { request =>
      onSuccess(bookService.getBook(request)) {
        case GetBookResponse.Found(book) =>
          complete(book)
        case GetBookResponse.NotFound =>
          complete(StatusCodes.NotFound)
      }
    }

  private def listBooks: Route =
    get {
      onSuccess(bookService.listBooks) { response =>
        complete(response.books)
      }
    }

  private def createBook: Route = {
    (post & entity(as[Book]).map(CreateBookRequest)) { request =>
      onSuccess(bookService.createBook(request)) {
        case CreateBookResponse.Created(book) =>
          complete(StatusCodes.Created, book)
        case CreateBookResponse.Invalid =>
          complete(StatusCodes.UnprocessableEntity)
      }
    }
  }
  private def updateBook: Route = {
    (put & path(JavaUUID) & entity(as[Book]).map(UpdateBookRequest)) { (_, request) =>
      onSuccess(bookService.updateBook(request)) {
        case UpdateBookResponse.Updated(book) =>
          complete(book)
        case UpdateBookResponse.NotFound =>
          complete(StatusCodes.NotFound)
      }
    }
  }

  private def deleteBook: Route = {
    (delete & path(JavaUUID).map(DeleteBookRequest)) { request =>
      onSuccess(bookService.deleteBook(request)) {
        case DeleteBookResponse.Deleted(book) =>
          complete(StatusCodes.Found, book)
        case DeleteBookResponse.NotFound =>
          complete(StatusCodes.NotFound)
      }
    }
  }
}
