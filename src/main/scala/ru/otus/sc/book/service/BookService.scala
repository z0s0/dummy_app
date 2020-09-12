package ru.otus.sc.book.service

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

trait BookService {
  def get(request: GetBookRequest): GetBookResponse
  def listBooks(): Seq[Book]
  def createBook(request: CreateBookRequest): CreateBookResponse
  def updateBook(request: UpdateBookRequest): UpdateBookResponse
  def deleteBook(request: DeleteBookRequest): DeleteBookResponse
}
