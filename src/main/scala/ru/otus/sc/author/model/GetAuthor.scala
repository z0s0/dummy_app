package ru.otus.sc.author.model

import java.util.UUID

sealed trait GetAuthorResponse

case object GetAuthorResponse {
  case class Found(author: Author) extends GetAuthorResponse
  case object NotFound             extends GetAuthorResponse
}

case class GetAuthorRequest(id: UUID)

case class ListAuthorsResponse(authors: Vector[Author])
