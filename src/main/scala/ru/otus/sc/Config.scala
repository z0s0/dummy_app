package ru.otus.sc

import zio.config.ReadError
import zio.{Has, Layer, Tag, ULayer, URLayer, ZLayer}
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config.typesafe.TypesafeConfig

final case class DbConfig(user: String, password: String, url: String)
final case class ApiConfig(host: String, port: Int)

final case class Config(apiConfig: ApiConfig, dbConfig: DbConfig)

object Config {
  val rootDescriptor = descriptor[Config]

  type AllConfigs = Has[DbConfig] with Has[ApiConfig]

  val live: Layer[ReadError[String], Has[Config]] = TypesafeConfig.fromDefaultLoader(rootDescriptor)

  val noErrors: ULayer[Has[Config]] = live.orDie

  val allConfigs: ULayer[AllConfigs] =
    noErrors >>> (subConfig(_.apiConfig) ++ subConfig(_.dbConfig))

  def subConfig[T: Tag](f: Config => T): URLayer[Has[Config], Has[T]] = ZLayer.fromService(f)
}
