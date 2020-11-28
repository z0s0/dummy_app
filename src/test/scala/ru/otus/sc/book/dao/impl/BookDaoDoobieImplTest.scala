package ru.otus.sc.book.dao.impl

import cats.effect.{Blocker, ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import ru.otus.sc.Config
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.dao.impl.AuthorDaoDoobieImpl
import ru.otus.sc.book.dao.{BookDao, BookDaoTest}
import ru.otus.sc.db.Migrations

class BookDaoDoobieImplTest extends BookDaoTest("BookDaoDoobie") with ForAllTestContainer {
  override val container: PostgreSQLContainer = PostgreSQLContainer()

  override def afterStart(): Unit = {
    super.afterStart()

    new Migrations(
      Config(
        dbPassword = container.password,
        dbUrl = container.jdbcUrl,
        dbUser = container.username
      )
    ).applyMigrationsSync()
  }

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  lazy val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    container.jdbcUrl,
    container.username,
    container.password,
    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )

  override def createAuthorDao: AuthorDao = {
    val dao = new AuthorDaoDoobieImpl(xa)
    dao.deleteAll().futureValue

    dao
  }

  override def createDao: BookDao = {
    val dao = new BookDaoDoobieImpl(xa)

    dao.deleteAll().futureValue

    dao
  }

}
