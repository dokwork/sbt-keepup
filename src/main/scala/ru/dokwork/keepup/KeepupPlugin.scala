package ru.dokwork.keepup

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import sbt.Keys._
import sbt._
import sbtrelease._
import sbtrelease.Vcs
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

import scala.io.Source
import scala.sys.process.ProcessLogger
import scala.util.matching.Regex
import scala.util.matching.Regex.{ Groups, Match }

object KeepupPlugin extends AutoPlugin {
  override def requires: Plugins = ReleasePlugin

  override def trigger = AllRequirements

  object autoImport {
    val releaseReadmeFile         = settingKey[Option[File]]("The path to the README file")
    val releaseReadmeVersionRegex = settingKey[Regex]("Regex pattern to find version in the README file")
    val updateReadme              = ReleaseStep(doUpdateReadme)
    val commitReadme              = ReleaseStep(doCommitReadme)
  }

  import autoImport._

  override def projectSettings = Seq[Setting[_]](
    releaseReadmeFile := None,
    releaseReadmeVersionRegex := """\%\s+\"(\d{1,2}\.\d{1,2}\.\d{1,2})\"""".r,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      updateReadme,
      commitReadme,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )

  def doUpdateReadme(st: State): State = {
    def replacer(newVersion: String)(m: Regex.Match): String = m match {
      case Groups(oldVersion) ⇒
        m.toString().replace(oldVersion, newVersion)
      case Match(_) ⇒
        newVersion
      case _ ⇒
        m.toString()
    }
    val extracted = Project.extract(st)
    extracted.get(autoImport.releaseReadmeFile) match {
      case Some(readmeFile) ⇒
        val releaseVersion = extracted.get(version)
        val versionRegex   = extracted.get(autoImport.releaseReadmeVersionRegex)
        val readme         = Source.fromFile(readmeFile).mkString
        Files.write(
          readmeFile.toPath,
          versionRegex.replaceAllIn(readme, replacer(releaseVersion)(_)).getBytes(StandardCharsets.UTF_8)
        )
        vcs(st).add()
      case None ⇒
    }
    st
  }

  def doCommitReadme(st: State): State = {
    val extracts = Project.extract(st)
    extracts.get(autoImport.releaseReadmeFile) match {
      case Some(readmeFile) ⇒
        val sign    = extracts.get(releaseVcsSign)
        val signOff = extracts.get(releaseVcsSignOff)
        vcs(st).add(readmeFile.getAbsolutePath) !! logger(st)
        vcs(st).commit("Version in the README updated.", sign, signOff) ! logger(st)
      case None ⇒
    }
    st
  }

  def vcs(st: State): Vcs = {
    Project
      .extract(st)
      .get(releaseVcs)
      .getOrElse(sys.error("Aborting release. Working directory is not a repository of a recognized VCS."))
  }

  def logger(st: State): ProcessLogger = new ProcessLogger {
    override def err(s: ⇒ String): Unit = st.log.info(s)
    override def out(s: ⇒ String): Unit = st.log.info(s)
    override def buffer[T](f: ⇒ T): T   = st.log.buffer(f)
  }
}
