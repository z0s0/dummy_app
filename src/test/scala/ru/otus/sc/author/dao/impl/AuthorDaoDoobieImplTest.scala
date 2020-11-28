package ru.otus.sc.author.dao.impl

import cats.effect.{Blocker, ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import ru.otus.sc.Config
import ru.otus.sc.author.dao.{AuthorDao, AuthorDaoTest}
import ru.otus.sc.db.Migrations

class AuthorDaoDoobieImplTest
    extends AuthorDaoTest("AuthorDaoDoobieImpl")
    with ForAllTestContainer {
  override def createEmptyDao: AuthorDao = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",                                    // driver classname
      container.jdbcUrl,                                          // connect URL (driver-specific)
      container.username,                                         // user
      container.password,                                         // password
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    val dao = new AuthorDaoDoobieImpl(xa)

    dao.deleteAll().futureValue

    dao
  }

  override val container: PostgreSQLContainer = PostgreSQLContainer()

  override def afterStart(): Unit = {
    super.afterStart()
    new Migrations(
      Config(
        dbPassword = container.password,
        dbUser = container.username,
        dbUrl = container.jdbcUrl
      )
    ).applyMigrationsSync()
  }

}
