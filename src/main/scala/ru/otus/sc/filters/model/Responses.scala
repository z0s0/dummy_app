package ru.otus.sc.filters.model

import ru.otus.sc.book.model.Book

sealed trait BooksByAuthorResponse

case class Found(books: Seq[Book]) extends BooksByAuthorResponse
