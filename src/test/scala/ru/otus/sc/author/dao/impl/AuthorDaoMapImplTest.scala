package ru.otus.sc.author.dao.impl

import ru.otus.sc.author.dao.{AuthorDao, AuthorDaoTest}
import ru.otus.sc.ThreadPool.CustomThreadPool

class AuthorDaoMapImplTest extends AuthorDaoTest(name = "AuthorDaoMapImplTest") {
  override def createEmptyDao: AuthorDao = new AuthorDaoMapImpl()
}
