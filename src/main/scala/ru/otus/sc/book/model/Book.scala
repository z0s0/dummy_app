package ru.otus.sc.book.model

import java.util.UUID

import ru.otus.sc.author.model.Genre

case class Book(
    id: UUID,
    name: String,
    authorName: String,
    genre: Genre = Genre.NoGenre,
    publishedYear: Int,
    pagesCount: Int
)
