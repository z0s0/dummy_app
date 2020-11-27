package ru.otus.sc.filter.service

import ru.otus.sc.filter.model.{
  FilterAuthorsRequest,
  FilterAuthorsResponse,
  FilterBooksRequest,
  FilterBooksResponse
}

import scala.concurrent.Future

object FilterService {
  val MinPagesCountToBeSignificant = 10
}

trait FilterService {
  def filterAuthors(request: FilterAuthorsRequest): Future[FilterAuthorsResponse]
  def filterBooks(request: FilterBooksRequest): Future[FilterBooksResponse]
}
