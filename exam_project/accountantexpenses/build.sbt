import Dependencies.*

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "AccountantExpenses",
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),

    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC1" % "test",
      "org.postgresql" % "postgresql" % "42.5.4",
      "org.typelevel" %% "cats-effect" % "3.5.0",
      "tf.tofu" %% "derevo-cats" % "0.13.0",
      "io.estatico" %% "newtype" % "0.4.4",
      "tf.tofu" %% "tofu-logging-log4cats" % "0.11.1" exclude("org.typelevel", "log4cats-core_2.13"),
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.2.10",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.5.0",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.4.0" exclude("org.http4s", "http4s-server_2.13"),
      "io.circe" %% "circe-generic-extras" % "0.14.3",
      "com.github.pureconfig" %% "pureconfig" % "0.17.4",
      "org.http4s" %% "http4s-ember-server" % "1.0.0-M21",
      "org.http4s" %% "http4s-blaze-server" % "1.0.0-M21",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-unchecked",
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:existentials",
      "-Xlint:_,-type-parameter-shadow"
    )
  )
