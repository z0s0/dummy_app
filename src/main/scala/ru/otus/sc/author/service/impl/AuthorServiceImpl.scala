package ru.otus.sc.author.service.impl

import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.service.AuthorService
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

import scala.concurrent.{ExecutionContext, Future}

class AuthorServiceImpl(dao: AuthorDao)(implicit val ThreadPool: ExecutionContext)
    extends AuthorService {
  override def listAuthors: Future[ListAuthorsResponse] = dao.listAuthors.map(ListAuthorsResponse)

  override def getAuthor(request: GetAuthorRequest): Future[GetAuthorResponse] = {
    dao.getAuthor(request.id).map {
      case Some(author) => GetAuthorResponse.Found(author)
      case None         => GetAuthorResponse.NotFound
    }
  }

  override def createAuthor(request: CreateAuthorRequest): Future[CreateAuthorResponse] = {
    dao.createAuthor(request.author).map {
      case Some(createdAuthor) => CreateAuthorResponse.Created(createdAuthor)
      case None                => CreateAuthorResponse.Invalid
    }
  }

  override def updateAuthor(request: UpdateAuthorRequest): Future[UpdateAuthorResponse] = {
    request.author.id match {
      case Some(_) =>
        dao.updateAuthor(request.author).map {
          case Some(updatedAuthor) => UpdateAuthorResponse.Updated(updatedAuthor)
          case None                => UpdateAuthorResponse.Invalid
        }
      case None => Future.successful(UpdateAuthorResponse.CannotUpdateWithoutID)
    }
  }

  override def deleteAuthor(request: DeleteAuthorRequest): Future[DeleteAuthorResponse] = {
    request.id match {
      case Some(id) =>
        dao.deleteAuthor(id).map {
          case Some(deletedAuthor) => DeleteAuthorResponse.Deleted(deletedAuthor)
          case None                => DeleteAuthorResponse.NotFound
        }
      case None => Future.successful(DeleteAuthorResponse.CannotDeleteWithoutID)
    }
  }
}
