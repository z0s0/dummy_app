package ru.otus.sc

case class Config(
    dbUser: String,
    dbPassword: String,
    dbUrl: String
)

object Config {
  def default: Config =
    Config(
      dbPassword = "smth",
      dbUrl = "jdbc:postgresql://localhost:5432/bookshelf",
      dbUser = "bookshelf"
    )
}
