package ru.otus.sc.filters.model

import ru.otus.sc.author.model.Genre

sealed trait AuthorsFilter

object AuthorsFilter {
  case class ByName(name: String)         extends AuthorsFilter
  case class ByGenre(genre: Genre)        extends AuthorsFilter
  case class ByPublicationYear(year: Int) extends AuthorsFilter
}
