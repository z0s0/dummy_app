import ru.otus.sc.Config
import ru.otus.sc.db.Migrations
import zio.ZLayer
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console

object DI {
  type Env = Blocking with Console with Clock
  val live =
    (Config.allConfigs ++ ZLayer.requires[Env]) >>>
      (Migrations.live ++ Blocking.live) >>>
      Migrations.withMigrations ++ Config.allConfigs
}
