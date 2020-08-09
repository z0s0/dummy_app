package ru.otus.sc.greet.dao

trait GreetingDao {
  def greetingPrefix: String
  def greetingPostfix: String
  def isKnownPerson(name: String): Boolean
}
