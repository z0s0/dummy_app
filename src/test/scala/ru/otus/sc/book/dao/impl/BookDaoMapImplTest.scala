package ru.otus.sc.book.dao.impl

import ru.otus.sc.book.dao.BookDaoTest
import ru.otus.sc.ThreadPool.CustomThreadPool

class BookDaoMapImplTest extends BookDaoTest("BookDaoMapImplTest", () => new BookDaoMapImpl)
