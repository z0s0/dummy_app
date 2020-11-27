package ru.otus.sc.author.dao.impl

import ru.otus.sc.author.dao.AuthorDaoTest
import ru.otus.sc.ThreadPool.CustomThreadPool

class AuthorDaoMapImplTest
    extends AuthorDaoTest(name = "AuthorDaoMapImplTest", () => new AuthorDaoMapImpl())
