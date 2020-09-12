package ru.otus.sc.book.service.impl

import java.util.UUID

import ru.otus.sc.author.model.Genre
import ru.otus.sc.book.service.BookService
import ru.otus.sc.book.dao.BookDao
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

class BookServiceImpl(dao: BookDao) extends BookService {
  private def isValidRequest(request: CreateBookRequest): Boolean = {
    val year = for {
      _    <- request.name
      _    <- request.authorName
      year <- request.publishedYear
    } yield year

    year match {
      case Some(_) => true
      case None    => false
    }
  }

  def get(request: GetBookRequest): GetBookResponse = {
    dao.getBook(request.id) match {
      case Some(book) =>
        GetBookResponse.Found(book)
      case None =>
        GetBookResponse.NotFound(request.id)
    }
  }

  def listBooks(): Seq[Book] = dao.listBooks()

  def createBook(request: CreateBookRequest): CreateBookResponse = {
    if (isValidRequest(request)) {
      CreateBookResponse.Created(
        dao.createBook(
          Book(
            id = UUID.randomUUID(),
            name = request.name.getOrElse(""),
            authorName = request.authorName.getOrElse(""),
            pagesCount = request.pagesCount.getOrElse(1),
            publishedYear = request.publishedYear.getOrElse(0),
            genre = request.genre.getOrElse(Genre.NoGenre)
          )
        )
      )
    } else {
      CreateBookResponse.Invalid
    }

  }

  def deleteBook(request: DeleteBookRequest): DeleteBookResponse = {
    request.id match {
      case Some(id) =>
        dao.deleteBook(id) match {
          case Some(book) =>
            DeleteBookResponse.Deleted(book)
          case None =>
            DeleteBookResponse.NotFound(id)
        }

      case None =>
        DeleteBookResponse.CantBeDeletedWithoutID
    }
  }

  def updateBook(request: UpdateBookRequest): UpdateBookResponse = {
    dao.updateBook(request.book) match {
      case Some(book) =>
        UpdateBookResponse.Updated(book)
      case None =>
        UpdateBookResponse.NotFound(request.book.id)
    }
  }
}
