import scala.io.Source
import sbtrelease.ReleaseStateTransformations._

releaseReadmeFile := Some(baseDirectory.value / "README.md")

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
      |Casual version mentioning 0.0.1
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
