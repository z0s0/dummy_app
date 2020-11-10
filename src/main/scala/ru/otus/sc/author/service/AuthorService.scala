package ru.otus.sc.author.service

import ru.otus.sc.author.model.{
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  GetAuthorRequest,
  GetAuthorResponse,
  ListAuthorsResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}

import scala.concurrent.Future

trait AuthorService {
  def listAuthors: Future[ListAuthorsResponse]
  def getAuthor(request: GetAuthorRequest): Future[GetAuthorResponse]
  def createAuthor(request: CreateAuthorRequest): Future[CreateAuthorResponse]
  def updateAuthor(request: UpdateAuthorRequest): Future[UpdateAuthorResponse]
  def deleteAuthor(request: DeleteAuthorRequest): Future[DeleteAuthorResponse]
}
