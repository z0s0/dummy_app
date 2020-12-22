import RootActor.SpawnAuthenticator
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.typed.Cluster
import akka.actor.typed.scaladsl.AskPattern._
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import akka.management.scaladsl.AkkaManagement
import akka.util.Timeout
import ru.otus.sc.auth.AuthenticatorActor

import scala.concurrent.duration._

object RootActor {
  sealed trait Command
  case class SpawnAuthenticator(replyTo: ActorRef[ActorRef[AuthenticatorActor.Command]])
      extends Command
  case object Stop extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>
      Behaviors.receiveMessage {
        case Stop => Behaviors.stopped
        case SpawnAuthenticator(replyTo) =>
          replyTo ! ctx.spawnAnonymous(AuthenticatorActor("Authenticator"))
          Behaviors.same
      }
    }
}

case class App(sys: ActorSystem[_])

object App {
  val system                                               = ActorSystem(RootActor(), "bookshelf")
  private implicit val sys: ActorSystem[RootActor.Command] = system
  private val cluster                                      = Cluster(system)

  def spawnAuthenticators() = {
    implicit val t: Timeout = 10.seconds
    (1 to 10).foreach(_ => {
      println("SPAWNING")
      system ? SpawnAuthenticator
    })
  }

  def hasRole(role: String): Boolean =
    cluster.selfMember.roles.contains(role)

  def start() = {
    if (hasRole("management")) AkkaManagement(system).start()

    if (hasRole("auth")) spawnAuthenticators()

    val sharding = ClusterSharding(system)

    sharding.init(
      Entity(AuthenticatorActor.typeKey)(ctx => AuthenticatorActor("Authenticator"))
        .withStopMessage(AuthenticatorActor.Stop)
    )

    App(system)
  }
}
