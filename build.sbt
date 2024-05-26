val scala3Version = "3.4.2"

val PekkoVersion = "1.0.2"
val PekkoHttpVersion = "1.0.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "pekko-http-sandbox",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
      "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion
    )
  )
