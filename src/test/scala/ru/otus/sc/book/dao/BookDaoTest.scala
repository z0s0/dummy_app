package ru.otus.sc.book.dao

import ru.otus.sc.author.model.Genre
import ru.otus.sc.book.model.Book

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

abstract class BookDaoTest
    extends AnyFreeSpec
    with ScalaFutures
    with ScalaCheckDrivenPropertyChecks {

  implicit lazy val genGenre: Gen[Genre]             = Gen.oneOf(Genre.NoGenre, Genre.Programming, Genre.Horror)
  implicit lazy val arbitraryGenre: Arbitrary[Genre] = Arbitrary(genGenre)

  implicit lazy val genBook: Gen[Book] = for {
    id            <- Gen.uuid
    bookName      <- arbitrary[String]
    authorName    <- arbitrary[String]
    genre         <- arbitrary[Genre]
    publishedYear <- arbitrary[Int]
    pagesCount    <- arbitrary[Int]
  } yield Book(
    id = Some(id),
    name = bookName,
    authorName = authorName,
    genre = genre,
    publishedYear = publishedYear,
    pagesCount = pagesCount
  )

  implicit lazy val arbitraryBook: Arbitrary[Book] = Arbitrary(genBook)
}
