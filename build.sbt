import sbtassembly.MergeStrategy
name := "ScalaTinkoffInvestServer"

version := "0.1"

scalaVersion := "2.13.2"

lazy val akkaVersion       = "2.6.4"
lazy val akkaHTTP          = "10.1.11"
lazy val logBackVersion    = "1.2.3"
lazy val catsVersion       = "2.0.0"
lazy val catsEffectVersion = "2.0.0"
lazy val fs2Version        = "2.0.1"
lazy val quillVersion      = "3.4.10"
lazy val softwaremill      = "2.3.3"
lazy val tinkoffapi        = "0.4.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka"    %% "akka-actor"              % akkaVersion,
  "com.typesafe.akka"    %% "akka-testkit"            % akkaVersion % Test,
  "com.typesafe.akka"    %% "akka-stream"             % akkaVersion,
  "com.typesafe.akka"    %% "akka-stream-testkit"     % akkaVersion % Test,
  "com.typesafe.akka"    %% "akka-http"               % akkaHTTP,
  "com.typesafe.akka"    %% "akka-http-testkit"       % akkaHTTP % Test,
  "com.typesafe.akka"    %% "akka-persistence"        % akkaVersion,
  "com.typesafe.akka"    %% "akka-distributed-data"   % akkaVersion,
  "com.typesafe.akka"    %% "akka-slf4j"              % akkaVersion,
  "ch.qos.logback"       % "logback-classic"          % logBackVersion,
  "ch.qos.logback"       % "logback-core"             % logBackVersion,
  "net.logstash.logback" % "logstash-logback-encoder" % "6.2"
)

libraryDependencies ++= Seq(
  "com.softwaremill.macwire" %% "macrosakka" % softwaremill % "provided",
  "com.softwaremill.macwire" %% "macros" % softwaremill % "provided",
  "com.softwaremill.macwire" %% "proxy" % softwaremill,
  "com.softwaremill.macwire" %% "util" % softwaremill
)

libraryDependencies ++= Seq(
  "ru.tinkoff.invest" % "openapi-java-sdk" % tinkoffapi pomOnly(),
  "ru.tinkoff.invest" % "openapi-java-sdk-example" % tinkoffapi,
  "ru.tinkoff.invest" % "openapi-java-sdk-core" % tinkoffapi
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.typelevel" %% "cats-core"   % catsVersion,
  "co.fs2"        %% "fs2-core"    % fs2Version,
  "co.fs2"        %% "fs2-io"      % fs2Version
)

libraryDependencies ++= Seq(
  "io.getquill" %% "quill-jdbc-monix" % quillVersion,
  "io.getquill" %% "quill-core" % quillVersion,
  "io.getquill" %% "quill-sql" % quillVersion,
  "mysql" % "mysql-connector-java" % "8.0.18"
)

libraryDependencies += "io.projectreactor" % "reactor-core" % "3.3.5.RELEASE"

libraryDependencies += "org.telegram" % "telegrambots" % "4.7"

libraryDependencies += "org.jetbrains" % "annotations" % "19.0.0"

lazy val commonSettings = Seq(
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("ru.invest.AppStart"),
  )
assemblyMergeStrategy in assembly := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first}
