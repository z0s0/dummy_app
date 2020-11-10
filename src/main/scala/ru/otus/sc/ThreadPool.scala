package ru.otus.sc

import java.util.concurrent.ForkJoinPool

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object ThreadPool {
  implicit lazy val CustomThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new ForkJoinPool(8))
}
