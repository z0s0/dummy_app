package ru.otus.sc.auth

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Future

class AuthService(implicit sys: ActorSystem[_]) {

  def is_authenticated(username: String, password: String): Future[Boolean] = {
    val authenticatorRef =
      ClusterSharding(sys).entityRefFor(AuthenticatorActor.typeKey, "Authenticator")

    implicit val t: Timeout = 10.seconds

    authenticatorRef.ask(ref => AuthenticatorActor.Authenticate(username, password, ref))
  }
}
