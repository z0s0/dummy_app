import zio.{Has, UIO, ULayer, ZIO, ZLayer}

object ZioMain {
  def main(args: Array[String]): Unit = {
    val program = for {
      foo <- ZIO.service[String]
      _   <- UIO(println(foo))
    } yield foo

    val runtime = zio.Runtime.default

    val withString: ULayer[Has[String]] = ZLayer.succeed("pidor")

    val programWithLayers = program.provideLayer(withString)

    runtime.unsafeRun(programWithLayers)

  }
}
