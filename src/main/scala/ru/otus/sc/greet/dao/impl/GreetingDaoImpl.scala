package ru.otus.sc.greet.dao.impl

import ru.otus.sc.greet.dao.GreetingDao

class GreetingDaoImpl extends GreetingDao {
  val knownPersons = Set("Serega", "Vova", "John")

  val greetingPrefix: String  = "Hi"
  val greetingPostfix: String = "!"

  def isKnownPerson(name: String): Boolean = knownPersons.contains(name)
}
