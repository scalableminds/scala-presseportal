import sbt._
import sbt.Keys._
import com.typesafe.config._
import scala.Some
import xerial.sbt.Sonatype.SonatypeKeys._

object Publish {
  lazy val settings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    organization := "com.scalableminds",
    organizationName := "scalable minds UG (haftungsbeschränkt) & Co. KG",
    organizationHomepage := Some(url("http://scalableminds.com")),
    startYear := Some(2014),
    profileName := "com.scalableminds",
    description := "Play framework 2.x module to execute mongo DB evolutions via comand line",
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/sclableminds/scala-presseportal")),
    scmInfo := Some(ScmInfo(url("https://github.com/sclableminds/scala-presseportal"), "https://github.com/scalableminds/scala-presseportal.git")),
    pomExtra := (
      <developers>
        <developer>
          <id>lesnail</id>
          <name>Thomas Werkmeister</name>
          <email>thomas.werkmeister@scalableminds.com</email>
          <url>http://github.com/lesnail</url>
        </developer>
      </developers>
      )
  )
}

object ApplicationBuild extends Build {

  val dependencies = Seq("io.spray" % "spray-client" % "1.2.0",
    "io.spray" % "spray-http" % "1.2.0",
    "io.spray" % "spray-httpx" % "1.2.0",
    "io.spray" % "spray-can" % "1.2.0",
    "io.spray" % "spray-util" % "1.2.0",
    "com.typesafe.akka" %% "akka-actor" % "2.2.3",
    "com.typesafe.play" % "play-json_2.10" % "2.2.1",
    "com.github.nscala-time" %% "nscala-time" % "0.8.0")

  val libraryResolvers = Seq( "spray repo" at "http://repo.spray.io",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

  val appVersion = "1.0.0"

  val name = "scala-presseportal"

  val presseportal = Project(name, file("."), settings = Project.defaultSettings ++
    xerial.sbt.Sonatype.sonatypeSettings ++
    Publish.settings ++
    Seq(version := appVersion,
        resolvers ++= libraryResolvers,
        libraryDependencies ++= dependencies))
}