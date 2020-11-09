package ru.otus.sc

import ru.otus.sc.greet.dao.impl.GreetingDaoImpl
import ru.otus.sc.greet.model.{GreetRequest, GreetResponse}
import ru.otus.sc.greet.service.GreetingService
import ru.otus.sc.greet.service.impl.GreetingServiceImpl

trait App {
  def greet(request: GreetRequest): GreetResponse
  def echo(request: GreetRequest): GreetRequest
  def isKnownPerson(request: GreetRequest): Boolean
  def humansGreeted(): Int
}

object App {
  private class AppImpl(greeting: GreetingService) extends App {
    def greet(request: GreetRequest): GreetResponse = greeting.greet(request)

    def echo(request: GreetRequest): GreetRequest = greeting.echo(request)

    def humansGreeted(): Int = greeting.humansGreeted()

    def isKnownPerson(request: GreetRequest): Boolean = greeting.isKnownPerson(request)
  }

  def apply(): App = {
    val greetingDao     = new GreetingDaoImpl
    val greetingService = new GreetingServiceImpl(greetingDao)
    new AppImpl(greetingService)
  }
}
