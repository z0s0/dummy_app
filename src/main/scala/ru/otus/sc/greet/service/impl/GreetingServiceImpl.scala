package ru.otus.sc.greet.service.impl

import ru.otus.sc.greet.dao.GreetingDao
import ru.otus.sc.greet.model.{GreetRequest, GreetResponse}
import ru.otus.sc.greet.service.GreetingService

class GreetingServiceImpl(dao: GreetingDao) extends GreetingService {
  var humansGreetedCounter = 0

  def greet(request: GreetRequest): GreetResponse =
    if (request.isHuman) {
      incHumansGreetedCounter()
      GreetResponse(
        s"${dao.greetingPrefix} ${request.name} ${dao.greetingPostfix}. You are ${humansGreetedCounter}th human found"
      )
    } else GreetResponse("AAAAAAAAAA!!!!!!")

  def echo(request: GreetRequest): GreetRequest = request

  def humansGreeted(): Int = humansGreetedCounter

  def isKnownPerson(request: GreetRequest): Boolean = dao.isKnownPerson(request.name)

  private def incHumansGreetedCounter(): Unit = humansGreetedCounter += 1
}
