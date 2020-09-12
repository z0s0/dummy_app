package ru.otus.sc.author.service

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

trait AuthorService {
  def get(request: GetAuthorRequest): GetAuthorResponse
  def list(): Seq[Author]
  def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse
  def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse
  def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse
}
