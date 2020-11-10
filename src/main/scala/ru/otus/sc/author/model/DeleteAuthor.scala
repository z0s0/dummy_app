package ru.otus.sc.author.model

import java.util.UUID

sealed trait DeleteAuthorResponse

case object DeleteAuthorResponse {
  case class Deleted(author: Author) extends DeleteAuthorResponse
  case object NotFound               extends DeleteAuthorResponse
  case object CannotDeleteWithoutID  extends DeleteAuthorResponse
}

case class DeleteAuthorRequest(id: Option[UUID])
