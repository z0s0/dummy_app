package ru.otus.sc

import ru.otus.sc.author.model.Genre

object ModelHelpers {
  def genreFromString(str: String): Genre = {
    str match {
      case "programming" => Genre.Programming
      case "horror"      => Genre.Horror
      case "no"          => Genre.NoGenre
    }
  }

  def stringFromGenre(genre: Genre): String = {
    genre match {
      case Genre.Programming => "programming"
      case Genre.Horror      => "horror"
      case Genre.NoGenre     => "no"
    }
  }
}
