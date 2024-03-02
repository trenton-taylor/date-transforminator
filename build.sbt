val circeVersion = "0.14.6"
val http4sVersion = "0.23.25"
val logbackVersion = "1.4.14"
val mUnitCatsEffectVersion = "1.0.7"
val mUnitVersion = "0.7.29"
val scalaLoggingVersion = "3.9.5"
val typesafeVersion = "1.4.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.bluechipfinancial",
    name := "date-transforminator",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "3.3.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.scalameta" %% "munit" % mUnitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % mUnitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
      "com.typesafe" % "config" % typesafeVersion
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )
