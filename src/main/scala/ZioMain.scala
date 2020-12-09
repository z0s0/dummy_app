import ru.otus.sc.{ApiConfig, Config, DbConfig}
import zio.{Has, UIO, ULayer, ZIO, ZLayer}

object ZioMain {
  def main(args: Array[String]): Unit = {
    val program = for {
      a <- ZIO.service[ApiConfig]
      b <- ZIO.service[DbConfig]
      _ <- UIO(println(a))
      _ <- UIO(println(b))
    } yield ()

    val runtime = zio.Runtime.default

    val programWithLayers = program.provideLayer(DI.live)

    runtime.unsafeRun(programWithLayers)

  }
}
