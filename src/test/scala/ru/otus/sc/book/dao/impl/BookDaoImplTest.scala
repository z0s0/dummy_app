package ru.otus.sc.book.dao.impl

import ru.otus.sc.book.dao.BookDaoTest
import ru.otus.sc.book.dao.impl.BookDaoImpl

class BookDaoImplTest
    extends BookDaoTest(name = "BookDaoImplTest", createDao = () => new BookDaoImpl)
