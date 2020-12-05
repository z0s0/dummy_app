package ru.otus.sc.book.json

import play.api.libs.json.{Json, OFormat}
import ru.otus.sc.author.model.Genre
import ru.otus.sc.book.model.Book
import ru.otus.sc.serialization.json.AdtProtocol

trait BookJsonProtocol extends AdtProtocol {
  implicit lazy val bookFormat: OFormat[Book] = Json.format

  implicit lazy val genreFormat: OFormat[Genre] = {
    implicit lazy val programmingFormat: OFormat[Genre.Programming.type] = objectFormat(
      Genre.Programming
    )
    implicit lazy val noGenreFormat: OFormat[Genre.NoGenre.type] = objectFormat(Genre.NoGenre)
    implicit lazy val horrorFormat: OFormat[Genre.Horror.type]   = objectFormat(Genre.Horror)

    adtFormat("genre")(
      adtCase[Genre.Programming.type]("Programming"),
      adtCase[Genre.Horror.type]("Horror"),
      adtCase[Genre.NoGenre.type]("No")
    )
  }
}

object BookJsonProtocol extends BookJsonProtocol
