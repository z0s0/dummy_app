package ru.otus.sc.author.model

import java.util.UUID

case class UpdateAuthorRequest(author: Author)

sealed trait UpdateAuthorResponse

object UpdateAuthorResponse {
  case class NotFound(id: UUID)      extends UpdateAuthorResponse
  case class Updated(author: Author) extends UpdateAuthorResponse
  case object CantUpdateWithoutID    extends UpdateAuthorResponse
}
