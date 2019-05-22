# Keep up
[![Build Status](https://travis-ci.org/dokwork/sbt-keepup.svg?branch=master)](https://travis-ci.org/dokwork/sbt-keepup)
[ ![Download](https://api.bintray.com/packages/dokwork/sbt-plugins/sbt-keepup/images/download.svg) ](https://bintray.com/dokwork/sbt-plugins/sbt-keepup/_latestVersion)

This library is extension for the [sbt-release plugin](https://github.com/sbt/sbt-release) which adds new release steps to keep up version of your libraries 
in the README file.

## Installation
Add to the `project/plugins.sbt`:
```scala
addSbtPlugin("ru.dokwork" % "sbt-keepup" % "0.1.0")
``` 

## How to use:

Just specify `README` file:
```scala
releaseReadmeFile := Some(baseDirectory.value / "README.md")
```

## Custom regex pattern

Default regex is: `\%\s+\"(\d{1,2}\.\d{1,2}\.\d{1,2})\"`. If you want change it, you should specify setting:
```scala
releaseReadmeVersionRegex := """\d{1,2}\.\d{1,2}\.\d{1,2}""".r
```
This plugin tries to set the current version of the project as the first found group or replace matched part entirely.



### PR to the original project

https://github.com/sbt/sbt-release/pull/217

https://github.com/muuki88/sbt-release/pull/1
