import sbt._

object Deps {

  object V {
    val akkaV = "2.6.20"
    val sttpV = "3.8.0"
    val bitcoinsV = "1.9.4-1-6b479e87-SNAPSHOT"

    val calibanV = "2.0.1"

    val grizzledSlf4jV = "1.3.4"
  }

  object Compile {

    val sttp =
      "com.softwaremill.sttp.client3" %% "akka-http-backend" % V.sttpV withSources () withJavadoc ()

    val akkaStreams =
      "com.typesafe.akka" %% "akka-stream" % V.akkaV withSources () withJavadoc ()

    val akkaActor =
      "com.typesafe.akka" %% "akka-actor-typed" % V.akkaV withSources () withJavadoc ()

    val grizzledSlf4j =
      "org.clapper" %% "grizzled-slf4j" % V.grizzledSlf4jV withSources () withJavadoc ()

    val caliban =
      "com.github.ghostdogpr" %% "caliban-client" % V.calibanV withSources () withJavadoc ()

    val bitcoinsAppCommons =
      "org.bitcoin-s" %% "bitcoin-s-app-commons" % V.bitcoinsV withSources () withJavadoc ()

    val bitcoinsTestkit =
      "org.bitcoin-s" %% "bitcoin-s-testkit" % V.bitcoinsV % Test withSources () withJavadoc ()
  }

  val client: List[ModuleID] = List(
    Compile.bitcoinsAppCommons,
    Compile.caliban,
    Compile.akkaActor,
    Compile.akkaStreams,
    Compile.sttp,
    Compile.bitcoinsTestkit,
    Compile.grizzledSlf4j
  )

}
