name := """latin"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  evolutions
)

libraryDependencies += "com.typesafe.play" %% "anorm" % "2.4.0"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1203-jdbc42"

routesGenerator := InjectedRoutesGenerator
