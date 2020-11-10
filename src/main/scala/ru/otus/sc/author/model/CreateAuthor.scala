package ru.otus.sc.author.model

sealed trait CreateAuthorResponse

case object CreateAuthorResponse {
  case class Created(author: Author) extends CreateAuthorResponse
  case object Invalid                extends CreateAuthorResponse
}

case class CreateAuthorRequest(author: Author)
