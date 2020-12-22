import sbt._

object ProjectConfig {

  object versions {
    val akka        = "2.6.10"
    val `akka-http` = "10.2.1"

    val `akka-http-play-json` = "1.34.0"
    val `play-json`           = "2.9.0"

    val scalactic         = "3.2.0"
    val scalatest         = "3.2.0"
    val scalacheck        = "1.14.3"
    val `scalacheck-1-14` = "3.2.0.0"
    val scalamock         = "5.0.0"

    val `scalacheck-shapeless_1.14` = "1.2.3"

    val doobie = "0.9.0"

    val slick = "3.3.2"

    val postgresql = "42.2.15"

    val `flyway-core` = "6.5.5"

    val logback = "1.2.3"

    val slf4j = "1.7.30"

    val testcontainers = "0.38.1"

    val tapir               = "0.17.0-M9"
    val managementVersion   = "1.0.8"
    val akkaPersistenceJDBC = "3.5.2"

  }

  val testDependencies = Seq(
    "org.scalactic"              %% "scalactic"                 % versions.scalactic                   % Test,
    "org.scalatest"              %% "scalatest"                 % versions.scalatest                   % Test,
    "org.scalacheck"             %% "scalacheck"                % versions.scalacheck                  % Test,
    "org.scalatestplus"          %% "scalacheck-1-14"           % versions.`scalacheck-1-14`           % Test,
    "org.scalamock"              %% "scalamock"                 % versions.scalamock                   % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14` % Test
  )

  val tapirDeps = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"     % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-core"                 % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-play"            % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"         % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"   % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % versions.tapir
  )

  val akkaDependencies = Seq(
    "com.typesafe.akka"             %% "akka-stream"                  % versions.akka,
    "com.typesafe.akka"             %% "akka-http"                    % versions.`akka-http`,
    "com.typesafe.akka"             %% "akka-stream-testkit"          % versions.akka,
    "com.typesafe.akka"             %% "akka-http-testkit"            % versions.`akka-http`,
    "com.typesafe.akka"             %% "akka-slf4j"                   % versions.`akka`,
    "com.typesafe.akka"             %% "akka-actor-typed"             % versions.`akka`,
    "com.typesafe.akka"             %% "akka-cluster-typed"           % versions.`akka`,
    "com.typesafe.akka"             %% "akka-cluster-sharding-typed"  % versions.`akka`,
    "com.typesafe.akka"             %% "akka-persistence-typed"       % versions.`akka`,
    "com.typesafe.akka"             %% "akka-serialization-jackson"   % versions.`akka`,
    "com.typesafe.akka"             %% "akka-persistence-query"       % versions.`akka`,
    "com.typesafe.akka"             %% "akka-cluster"                 % versions.`akka`,
    "com.typesafe.akka"             %% "akka-pki"                     % versions.`akka`,
    "com.typesafe.akka"             %% "akka-distributed-data"        % versions.`akka`,
    "com.typesafe.akka"             %% "akka-remote"                  % versions.`akka`,
    "com.typesafe.akka"             %% "akka-coordination"            % versions.`akka`,
    "com.typesafe.akka"             %% "akka-cluster-tools"           % versions.`akka`,
    "com.lightbend.akka.management" %% "akka-management"              % versions.managementVersion,
    "com.lightbend.akka.management" %% "akka-management-cluster-http" % versions.managementVersion,
    "com.typesafe.akka"             %% "akka-persistence"             % versions.`akka`,
    "com.github.dnvriend"           %% "akka-persistence-jdbc"        % versions.akkaPersistenceJDBC,
    "com.typesafe.akka"             %% "akka-testkit"                 % versions.`akka`,
    "com.typesafe.akka"             %% "akka-http-spray-json"         % versions.`akka-http`
  )

  val doobieDependencies = Seq(
    "org.tpolecat" %% "doobie-core"      % versions.doobie,
    "org.tpolecat" %% "doobie-hikari"    % versions.doobie,
    "org.tpolecat" %% "doobie-quill"     % versions.doobie,
    "org.tpolecat" %% "doobie-postgres"  % versions.doobie,
    "org.tpolecat" %% "doobie-scalatest" % versions.doobie % Test
  )

  val slickDependencies = Seq(
    "com.typesafe.slick" %% "slick"          % versions.slick,
    "com.typesafe.slick" %% "slick-hikaricp" % versions.slick
  )

  val dbDependencies = Seq(
    "org.postgresql" % "postgresql"                      % versions.postgresql,
    "org.flywaydb"   % "flyway-core"                     % versions.`flyway-core`,
    "com.dimafeng"  %% "testcontainers-scala-scalatest"  % versions.testcontainers % Test,
    "com.dimafeng"  %% "testcontainers-scala-postgresql" % versions.testcontainers % Test
  )

  val logDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "org.slf4j"      % "slf4j-api"       % versions.slf4j
  )

  val playJsonDependencies = Seq(
    "de.heikoseeberger" %% "akka-http-play-json" % versions.`akka-http-play-json`,
    "com.typesafe.play" %% "play-json"           % versions.`play-json`
  )

  val projectDependencies =
    testDependencies ++
      akkaDependencies ++
      playJsonDependencies ++
      doobieDependencies ++
      slickDependencies ++
      logDependencies ++
      dbDependencies ++
      tapirDeps
}
