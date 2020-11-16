package ru.otus.sc.book.service.impl

import ru.otus.sc.book.dao.BookDao
import ru.otus.sc.book.model.{
  CreateBookRequest,
  CreateBookResponse,
  DeleteBookRequest,
  DeleteBookResponse,
  GetBookRequest,
  GetBookResponse,
  ListBooksResponse,
  UpdateBookRequest,
  UpdateBookResponse
}
import ru.otus.sc.book.service.BookService

import scala.concurrent.{ExecutionContext, Future}

class BookServiceImpl(dao: BookDao)(implicit val ThreadPool: ExecutionContext) extends BookService {
  override def getBook(request: GetBookRequest): Future[GetBookResponse] = {
    dao.getBook(request.id).map {
      case Some(book) =>
        GetBookResponse.Found(book)
      case None => GetBookResponse.NotFound
    }
  }

  override def createBook(request: CreateBookRequest): Future[CreateBookResponse] = {
    dao.createBook(request.book).map {
      case Some(newBook) => CreateBookResponse.Created(newBook)
      case None          => CreateBookResponse.Invalid
    }
  }

  override def deleteBook(request: DeleteBookRequest): Future[DeleteBookResponse] = {
    dao.deleteBook(request.id).map {
      case Some(deletedBook) => DeleteBookResponse.Deleted(deletedBook)
      case None              => DeleteBookResponse.NotFound
    }
  }

  override def listBooks: Future[ListBooksResponse] = dao.listBooks.map(ListBooksResponse)

  override def updateBook(request: UpdateBookRequest): Future[UpdateBookResponse] =
    dao.updateBook(request.book).map {
      case Some(updatedBook) => UpdateBookResponse.Updated(updatedBook)
      case None              => UpdateBookResponse.NotFound
    }
}
