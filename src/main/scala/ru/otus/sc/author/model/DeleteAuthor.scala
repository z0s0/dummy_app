package ru.otus.sc.author.model

import java.util.UUID

case class DeleteAuthorRequest(id: UUID)

sealed trait DeleteAuthorResponse

object DeleteAuthorResponse {
  case class NotFound(id: UUID)      extends DeleteAuthorResponse
  case class Deleted(author: Author) extends DeleteAuthorResponse
}
