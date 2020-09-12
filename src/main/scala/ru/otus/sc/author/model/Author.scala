package ru.otus.sc.author.model

import java.util.UUID

sealed trait Genre

object Genre {
  case object Programming extends Genre
  case object Horror      extends Genre
  case object Management  extends Genre
  case object NoGenre     extends Genre
}

case class Author(
    id: UUID,
    name: String,
    genres: Set[Genre] = Set.empty
)
