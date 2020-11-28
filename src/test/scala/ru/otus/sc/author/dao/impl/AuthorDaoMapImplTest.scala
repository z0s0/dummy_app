package ru.otus.sc.author.dao.impl

import ru.otus.sc.author.dao.{AuthorDao, AuthorDaoTest}
import scala.concurrent.ExecutionContext.Implicits.global

class AuthorDaoMapImplTest extends AuthorDaoTest(name = "AuthorDaoMapImplTest") {
  override def createEmptyDao: AuthorDao = new AuthorDaoMapImpl()
}
