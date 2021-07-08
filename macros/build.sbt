/*
import sbt._
import Keys._

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

    "org.hibernate" % "hibernate-core" % hibernateVersion,
    "org.scala-lang" %  "scala-reflect" % scalaVersion.value % "provided",
    //    "org.scalamacros" %% "paradise" % paradiseVersion,
    //    "org.apache.commons" % "commons-lang3" % apacheCommonsVersion,

    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    //      compilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
    //   "io.jsonwebtoken" % "jjwt" % "0.9.1",
    //  "org.mindrot" % "jbcrypt" % "0.4"
  )
}
*/

scalaVersion := "2.12.12"
val paradiseVersion = "2.1.1"
val scalaTestVersion = "3.2.0"
val logBackVersion = "1.1.3"
val hibernateVersion = "5.4.3.Final"


lazy val macroSettings: Seq[Def.Setting[_]] = Seq(
  name := "ArrayMapMacros",
  organization := "mv.zem",
  version := "1.0",
  addCompilerPlugin("org.scalamacros" %% "paradise" % paradiseVersion cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
//  scalacOptions += s"-Xplugin:$unmanagedBase/scalac-plugin_2.12.3-2.0.0-96-9f738df2.jar",
  scalacOptions in (Compile, console) := Seq(),
  sources in (Compile, doc) := Nil,
  //macro paradise plug-in doesn't work in REPL yet.
  scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise"))/*,
  libraryDependencies ++= Seq(
                "org.scala-lang" % "scala-reflect" % scalaVersion.value,
                "ch.qos.logback" % "logback-classic" % logBackVersion,
                "org.scala-lang" % "scala-library" % scalaVersion.value,
                "org.scala-lang" %  "scala-reflect" % scalaVersion.value % "provided",
//    "org.scalamacros" %% "paradise" % paradiseVersion,
                "org.scalactic" %% "scalactic" % scalaTestVersion,
                "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
        )*/
)
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "ch.qos.logback" % "logback-classic" % logBackVersion,
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scala-lang" %  "scala-reflect" % scalaVersion.value % "provided",
  "org.hibernate" % "hibernate-core" % hibernateVersion,

  //    "org.scalamacros" %% "paradise" % paradiseVersion,
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

