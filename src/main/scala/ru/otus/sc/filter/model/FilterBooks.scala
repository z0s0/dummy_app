package ru.otus.sc.filter.model

import ru.otus.sc.book.model.Book

case class FilterBooksRequest(a: Int)

case class FilterBooksResponse(books: Seq[Book])
