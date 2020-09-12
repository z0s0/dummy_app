package ru.otus.sc.author.dao.impl

import ru.otus.sc.author.dao.AuthorDaoTest
import ru.otus.sc.author.dao.impl.AuthorDaoMapImpl

class AuthorDaoMapImplTest
    extends AuthorDaoTest(name = "AuthorDaoMapImplTest", createDao = () => new AuthorDaoMapImpl())
