package ru.otus.sc.author.model

import java.util.UUID

case class GetAuthorRequest(id: Option[UUID])

sealed trait GetAuthorResponse

object GetAuthorResponse {
  case class Found(author: Author) extends GetAuthorResponse
  case class NotFound(id: UUID)    extends GetAuthorResponse
  case object CantBeFoundWithoutID extends GetAuthorResponse
}
