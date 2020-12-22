package ru.otus.sc.author.json

import play.api.libs.json.{Json, OFormat}
import ru.otus.sc.author.model.{Author, Genre}
import ru.otus.sc.serialization.json.AdtProtocol

trait AuthorJsonProtocol extends AdtProtocol {
  implicit lazy val authorFormat: OFormat[Author] = Json.format

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

object AuthorJsonProtocol extends AuthorJsonProtocol
