package ru.otus.sc.book.dao.impl

import ru.otus.sc.ThreadPool.CustomThreadPool
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl
import ru.otus.sc.book.dao.{BookDao, BookDaoTest}

class BookDaoMapImplTest extends BookDaoTest("BookDaoMapImplTest") {
  override def createDao: BookDao = new BookDaoMapImpl

  override def createAuthorDao: AuthorDao = new AuthorDaoMapImpl
}
