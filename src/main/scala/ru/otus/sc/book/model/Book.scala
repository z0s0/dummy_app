package ru.otus.sc.book.model

import java.util.UUID

import ru.otus.sc.author.model.Genre

case class Book(
    id: Option[UUID],
    name: String,
    authorName: String,
    publishedYear: Int,
    pagesCount: Int,
    genre: Genre
)
