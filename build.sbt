name := "play23-slick3-silhouette"

version := "1.0"

lazy val `play23-slick3-silhouette` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq( cache , ws , evolutions, filters )

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "com.h2database" % "h2" % "1.4.187",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "net.ceedubs" % "ficus_2.11" % "1.1.2",
  "com.mohiva" % "play-silhouette-testkit_2.11" % "3.0.0" % "test"
)

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test"
)

libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  