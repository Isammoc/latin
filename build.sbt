name := """latin"""

version := "2.0-SNAPSHOT"

enablePlugins(PlayScala)

scalaVersion := "2.12.16"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  evolutions,
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "org.xerial" % "sqlite-jdbc" % "3.39.3.0",
)

routesGenerator := InjectedRoutesGenerator
