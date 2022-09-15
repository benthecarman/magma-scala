import sbt.Keys.excludeLintKeys
import xerial.sbt.Sonatype.GitHubHosting

import scala.util.Properties

val scala2_13 = "2.13.8"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/benthecarman/magma-scala"),
    "scm:git@github.com:benthecarman/magma-scala.git"
  )
)

ThisBuild / developers := List(
  Developer(
    "benthecarman",
    "Ben Carman",
    "benthecarman@live.com",
    url("https://twitter.com/benthecarman")
  )
)

ThisBuild / organization := "space.amboss"

ThisBuild / licenses := List(
  "MIT" -> new URL("https://opensource.org/licenses/MIT"))

ThisBuild / homepage := Some(url("https://github.com/benthecarman/magma-scala"))

ThisBuild / sonatypeProfileName := "space.amboss"

ThisBuild / sonatypeProjectHosting := Some(
  GitHubHosting("benthecarman", "magama-scala", "benthecarman@live.com"))

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

ThisBuild / scalafmtOnCompile := !Properties.envOrNone("CI").contains("true")

ThisBuild / scalaVersion := scala2_13

ThisBuild / crossScalaVersions := List(scala2_13)

ThisBuild / dynverSeparator := "-"

//https://github.com/sbt/sbt/pull/5153
Global / excludeLintKeys ++= Set(
  Keys.mainClass
)
