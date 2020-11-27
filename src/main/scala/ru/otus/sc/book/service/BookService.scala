package ru.otus.sc.book.service

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

import scala.concurrent.Future

trait BookService {
  def getBook(request: GetBookRequest): Future[GetBookResponse]
  def listBooks: Future[ListBooksResponse]
  def updateBook(request: UpdateBookRequest): Future[UpdateBookResponse]
  def createBook(request: CreateBookRequest): Future[CreateBookResponse]
  def deleteBook(request: DeleteBookRequest): Future[DeleteBookResponse]
}
