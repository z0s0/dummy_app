package ru.otus.sc.author.model

import java.util.UUID

sealed trait Genre

case object Genre {
  case object Programming extends Genre
  case object Horror      extends Genre
  case object NoGenre     extends Genre
}

case class Author(
    id: Option[UUID],
    name: String,
    genres: Set[Genre] = Set.empty
)
