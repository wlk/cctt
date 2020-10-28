enablePlugins(JavaAppPackaging)

name := "cctt"

version := "1.0"

scalaVersion := "2.13.3"

libraryDependencies ++= {
  val akkaHttpVersion = "10.2.1"
  val akkaVersion = "2.6.10"
  val xchangeVersion = "5.0.3"
  val scalatestVersion = "3.2.2"
  val logbackVersion = "1.2.3"
  val scalaLoggingVersion = "3.9.2"
  val catsVersion = "2.2.0"
  val pureconfigVersion = "0.14.0"
  val tapirVersion = "0.17.0-M5"

  val testDependencies = Seq(
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion  % Test,
    "org.scalatest"     %% "scalatest"         % scalatestVersion % Test
  )

  val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  )

  val xchangeDependencies = {
    val exchanges = Seq(
      "kraken"
    ).map(exchange => "org.knowm.xchange" % s"xchange-$exchange" % xchangeVersion)

    exchanges
  }

  val tapirDependencies = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"   % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-redoc-akka-http"    % tapirVersion
  )

  val otherDependencies = Seq(
    "ch.qos.logback"             % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging"  % scalaLoggingVersion,
    "org.typelevel"              %% "cats-core"      % catsVersion,
    "com.github.pureconfig"      %% "pureconfig"     % pureconfigVersion
  )

  testDependencies ++ otherDependencies ++ xchangeDependencies ++ akkaDependencies ++ tapirDependencies
}

scalacOptions --= Seq(
  "-Xfatal-warnings"
)

// don't generate scaladoc
publishArtifact in (Compile, packageDoc) := false
//publishArtifact in packageDoc := false
sources in (Compile, doc) := Seq.empty

addCommandAlias("testAll", ";test")
addCommandAlias("formatAll", ";scalafmt;test:scalafmt;scalafmtSbt")
addCommandAlias("compileAll", ";compile;test:compile")
