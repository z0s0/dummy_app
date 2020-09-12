package ru.otus.sc.author.model

case class CreateAuthorRequest(
    name: Option[String],
    genres: Option[Set[Genre]]
)

sealed trait CreateAuthorResponse

case object CreateAuthorResponse {
  case class Created(author: Author) extends CreateAuthorResponse
  case class Invalid(msg: String)    extends CreateAuthorResponse
}
