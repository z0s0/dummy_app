package ru.otus.sc.filter.model

import ru.otus.sc.author.model.Author

case class FilterAuthorsRequest(a: Int)

case class FilterAuthorsResponse(authors: Seq[Author])
