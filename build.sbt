name := "ScalaTinkoffInvestServer"

version := "0.1"

scalaVersion := "2.13.2"


libraryDependencies += "ru.tinkoff.invest" % "openapi-java-sdk-core" % "0.4.1"
libraryDependencies += "ru.tinkoff.invest" % "openapi-java-sdk" % "0.4.1" pomOnly()
libraryDependencies += "ru.tinkoff.invest" % "openapi-java-sdk-example" % "0.4.1"

lazy val akkaVersion = "2.6.4"
lazy val akkaHTTP = "10.1.11"
lazy val logBackVersion = "1.2.3"
lazy val catsVersion       = "2.0.0"
lazy val catsEffectVersion = "2.0.0"
lazy val fs2Version        = "2.0.1"
lazy val quillVersion      = "3.4.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHTTP,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHTTP % Test,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback"     %  "logback-classic" % logBackVersion,
  "ch.qos.logback" % "logback-core" % logBackVersion,
  "net.logstash.logback" % "logstash-logback-encoder" % "6.2"
)


libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "co.fs2" %% "fs2-core" % fs2Version,
  "co.fs2" %% "fs2-io" % fs2Version
)

libraryDependencies ++= Seq(
  "io.getquill" %% "quill-sql" % quillVersion,
  "io.getquill" %% "quill-core" % quillVersion,
  "io.getquill" %% "quill-jdbc-monix" % quillVersion,
  "mysql" % "mysql-connector-java" % "8.0.18"
)
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"

libraryDependencies += "com.softwaremill.macwire" %% "macrosakka" % "2.3.3" % "provided"

libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.3.3"

libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.3.3"