package ru.otus.sc.db

import org.flywaydb.core.Flyway
import ru.otus.sc.DbConfig
import zio.{Has, UIO, URIO, URLayer, ZIO, ZLayer}
import zio.blocking.{Blocking, effectBlocking}

class Migrations(config: DbConfig) {
  def applyMigrationsSync(): Unit = {
    Flyway
      .configure()
      .dataSource(config.url, config.user, config.password)
      .load()
      .migrate()
  }
}

object Migrations {
  type Migrations = Has[Service]

  trait Service {
    def applyMigrations(): URIO[Blocking, Unit]
  }

  sealed trait AfterMigrations
  type WithMigrations = Has[AfterMigrations]

  val live: URLayer[Has[DbConfig], Migrations] =
    ZLayer.fromService { dbConf =>
      new Service {
        override def applyMigrations(): URIO[Blocking, Unit] =
          effectBlocking {
            println("MIGRATIONS STARTED")

            Flyway
              .configure()
              .dataSource(dbConf.url, dbConf.user, dbConf.password)
              .load()
              .migrate()
          }.orDie.unit
      }
    }

  val withMigrations: ZLayer[Migrations with Blocking, Nothing, WithMigrations] =
    ZIO.service[Service].flatMap(_.applyMigrations()).as(new AfterMigrations {}).toLayer
}
