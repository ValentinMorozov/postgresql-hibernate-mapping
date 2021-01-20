//import sbt._
//import Keys._

val scalaTestVersion = "3.2.0"
val logBackVersion = "1.1.3"
val springVersion = "2.1.4.RELEASE"
val hibernateVersion = "5.4.3.Final"
val pgsqlVersion = "42.2.5"
val paradiseVersion = "2.1.1"

val buildSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "org.scalamacros",
  version := "1.0.0",
  scalacOptions ++= Seq(),
  scalaVersion := "2.12.12",
  crossScalaVersions := Seq("2.12.11"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
)

lazy val root = (project
  .in(file("."))
  .settings(buildSettings)
) aggregate(macros) dependsOn macros

lazy val macros = (project
  .in(file("macros"))
  .settings(buildSettings ++ Seq(
      libraryDependencies += scalaVersion("org.scala-lang" % "scala-reflect" % _).value,
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
        else Nil
        )
    )
  )
)

libraryDependencies ++= {
  Seq(
    "org.scala-lang" % "scala-library" % scalaVersion.toString(),
    "org.springframework.boot" % "spring-boot-autoconfigure" % springVersion,
    "org.springframework.boot" % "spring-boot-starter-data-jpa" % springVersion,
    "org.postgresql" % "postgresql" % pgsqlVersion,
    "org.hibernate" % "hibernate-core" % hibernateVersion,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  )
}
