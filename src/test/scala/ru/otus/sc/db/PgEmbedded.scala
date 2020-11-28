package ru.otus.sc.db

import org.scalatest.{BeforeAndAfterAll, Suite}

sealed trait SqlTypeMeta[T] {}

case class TypedParam[T](t: T, meta: SqlTypeMeta[T])

trait PgEmbedded extends BeforeAndAfterAll {
  this: Suite =>

  override protected def beforeAll(): Unit = {
    super.beforeAll()

  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }

}
