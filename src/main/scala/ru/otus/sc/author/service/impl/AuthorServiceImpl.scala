package ru.otus.sc.author.service.impl
import java.util.UUID

import ru.otus.sc.author.model.{
  Author,
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  Genre,
  GetAuthorRequest,
  GetAuthorResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}
import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.author.dao.AuthorDao

class AuthorServiceImpl(dao: AuthorDao) extends AuthorService {

  def list(): Seq[Author] = dao.listAuthors()

  def get(request: GetAuthorRequest): GetAuthorResponse = {
    request.id match {
      case None => GetAuthorResponse.CantBeFoundWithoutID
      case Some(id) =>
        dao.get(id) match {
          case None         => GetAuthorResponse.NotFound(id)
          case Some(author) => GetAuthorResponse.Found(author)
        }
    }
  }

  def createAuthor(request: CreateAuthorRequest): CreateAuthorResponse = {
    val params: Option[(String, Set[Genre])] = for {
      name   <- request.name
      genres <- request.genres
    } yield (name, genres)

    params match {
      case Some((name, genres)) =>
        CreateAuthorResponse.Created(
          dao.createAuthor(
            Author(
              id = UUID.randomUUID(),
              name = name,
              genres = genres
            )
          )
        )

      case None =>
        CreateAuthorResponse.Invalid(msg = "Name was not provided")
    }
  }

  def updateAuthor(request: UpdateAuthorRequest): UpdateAuthorResponse = {
    dao.updateAuthor(request.author) match {
      case Some(author) =>
        UpdateAuthorResponse.Updated(author)
      case None =>
        UpdateAuthorResponse.NotFound(request.author.id)
    }
  }

  def deleteAuthor(request: DeleteAuthorRequest): DeleteAuthorResponse = {
    dao
      .deleteAuthor(request.id)
      .map(DeleteAuthorResponse.Deleted)
      .getOrElse(DeleteAuthorResponse.NotFound(request.id))
  }

}
