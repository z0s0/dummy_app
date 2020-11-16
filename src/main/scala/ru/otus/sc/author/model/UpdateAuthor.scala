package ru.otus.sc.author.model

sealed trait UpdateAuthorResponse

case object UpdateAuthorResponse {
  case class Updated(author: Author) extends UpdateAuthorResponse
  case object Invalid                extends UpdateAuthorResponse
}

case class UpdateAuthorRequest(author: Author)
