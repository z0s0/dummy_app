package ru.otus.sc.book.dao.impl

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.otus.sc.book.dao.BookDaoTest

class BookDaoMapImplTest extends BookDaoTest("BookDaoMapImplTest", () => new BookDaoMapImpl)
