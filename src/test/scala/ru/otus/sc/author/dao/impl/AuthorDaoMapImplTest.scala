package ru.otus.sc.author.dao.impl

import ru.otus.sc.author.dao.AuthorDaoTest

class AuthorDaoMapImplTest
    extends AuthorDaoTest(name = "AuthorDaoMapImplTest", () => new AuthorDaoMapImpl())
