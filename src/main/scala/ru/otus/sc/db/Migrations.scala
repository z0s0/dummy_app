package ru.otus.sc.db

import org.flywaydb.core.Flyway
import ru.otus.sc.Config

class Migrations(config: Config) {
  def applyMigrationsSync(): Unit = {
    println("STARGING MIGRATIONS")

    Flyway
      .configure()
      .dataSource(config.dbUrl, config.dbUser, config.dbPassword)
      .load()
      .migrate()
  }
}
