import sbt.addSbtPlugin
import sbtrelease.ReleaseStateTransformations._

lazy val `sbt-keepup` = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    organization := "ru.dokwork",
    scalaVersion := "2.12.8",
    scalacOptions ++= Seq(
      "-encoding",
      "utf-8",
      "-target:jvm-1.8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xexperimental",
      "-Xlint",
      "-Ywarn-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-inaccessible",
      "-Ywarn-nullary-override",
      "-Ywarn-unused-import",
      "-language:higherKinds"
    ),
    sbtPlugin := true,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.10"),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  )
