package ru.otus.sc.auth

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.EntityTypeKey

object AuthenticatorActor {
  sealed trait Command
  case object Stop extends Command
  case class Authenticate(username: String, password: String, replyTo: ActorRef[Boolean])
      extends Command

  val serviceKey: ServiceKey[Command] = ServiceKey[Command]("Authenticator")
  val typeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("Authenticator")

  def apply(name: String): Behavior[Command] =
    Behaviors.setup { ctx =>
      ctx.system.receptionist ! Receptionist.Register(serviceKey, ctx.self)

      Behaviors.receiveMessage {
        case Stop => Behaviors.stopped
        case Authenticate(username, password, replyTo) =>
          println(s"RECEIVED for ${username} ${password}")
          replyTo ! is_authenticated(username, password)
          Behaviors.same
      }
    }

  private def is_authenticated(username: String, password: String): Boolean =
    (username == "serega") && (password == "serega")
}
