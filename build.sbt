ThisBuild / scalaVersion := "2.13.8"

lazy val `magma-scala` = (project in file("."))
  .settings(CommonSettings.prodSettings: _*)
  .settings(
    libraryDependencies ++= Deps.client,
    name := "magma-scala"
  )
  .settings(
    Compile / caliban / calibanSettings += calibanSetting(
      url("https://api.amboss.space/graphql")
    )(cs =>
      cs.clientName("Amboss")
        .packageName("space.amboss"))
  )
  .enablePlugins(CalibanPlugin)
