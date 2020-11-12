package ru.otus.sc.filter.model

import ru.otus.sc.author.model.{Author, Genre}

sealed trait AuthorsFilter

object AuthorsFilter {
  case class ByName(name: String)         extends AuthorsFilter
  case class ByGenre(genre: Genre)        extends AuthorsFilter
  case class ByPublicationYear(year: Int) extends AuthorsFilter
}

case class FilterAuthorsRequest(filters: List[AuthorsFilter])

case class FilterAuthorsResponse(authors: Seq[Author])
