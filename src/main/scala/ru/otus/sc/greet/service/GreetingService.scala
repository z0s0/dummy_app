package ru.otus.sc.greet.service

import ru.otus.sc.greet.model.{GreetRequest, GreetResponse}

trait GreetingService {
  def greet(request: GreetRequest): GreetResponse

  def echo(request: GreetRequest): GreetRequest

  def isKnownPerson(request: GreetRequest): Boolean

  def humansGreeted(): Int
}
