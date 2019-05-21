import scala.io.Source
import sbtrelease.ReleaseStateTransformations._

releaseReadmeFile := Some(baseDirectory.value / "README.md")
releaseReadmeVersionRegex := """\d{1,2}\.\d{1,2}\.\d{1,2}""".r

releaseProcess := Seq(
  inquireVersions,
  runClean,
  setReleaseVersion,
  updateReadme,
  commitReadme
)

TaskKey[Unit]("runTest") := {
  val readmeFile = releaseReadmeFile.value.get
  val expected =
    """# Update README test
      |
      |```scala
      |libraryDependencies += "com.example" %% "test" % "1.0.0"
      |```""".stripMargin
  val actual = Source.fromFile(readmeFile).mkString

  val delimeter = "=" * 10

  assert(
    actual == expected,
    s"""Unexpected README content after release:
    |$delimeter expected $delimeter
    |$expected
    |$delimeter actual $delimeter
    |$actual
    |${delimeter * 2}""".stripMargin
  )
}
