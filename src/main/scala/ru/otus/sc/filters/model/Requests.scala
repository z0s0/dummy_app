package ru.otus.sc.filters.model

case class FilterAuthorsRequest(filter: Option[Seq[AuthorsFilter]], limit: Option[Int])
case class FilterBooksRequest(filter: Option[Seq[BooksFilter]], limit: Option[Int])
