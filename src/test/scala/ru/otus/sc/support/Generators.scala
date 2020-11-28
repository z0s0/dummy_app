package ru.otus.sc.support

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import ru.otus.sc.author.model.{Author, Genre}
import ru.otus.sc.book.model.Book

object Generators {
  implicit lazy val genGenre: Gen[Genre]             = Gen.oneOf(Genre.NoGenre, Genre.Programming, Genre.Horror)
  implicit lazy val arbitraryGenre: Arbitrary[Genre] = Arbitrary(genGenre)

  implicit lazy val genBook: Gen[Book] = for {
    id            <- Gen.uuid
    bookName      <- arbitrary[String]
    authorName    <- arbitrary[String]
    genre         <- arbitrary[Genre]
    publishedYear <- Gen.choose(-5000, 2020)
    pagesCount    <- Gen.choose(1, 1000)
  } yield Book(
    id = Some(id),
    name = bookName,
    authorName = authorName,
    genre = genre,
    publishedYear = publishedYear,
    pagesCount = pagesCount
  )

  implicit lazy val arbitraryBook: Arbitrary[Book] = Arbitrary(genBook)

  implicit lazy val genAuthor: Gen[Author] = for {
    id    <- Gen.uuid
    name  <- arbitrary[String]
    genre <- arbitrary[Genre]
  } yield Author(
    id = Some(id),
    name = name,
    genres = Set(genre)
  )

  implicit lazy val arbitraryAuthor: Arbitrary[Author] = Arbitrary(genAuthor)
}
