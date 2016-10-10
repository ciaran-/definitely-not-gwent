name := "definitely-not-gwent"

version := "0.1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.11",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "io.spray" % "spray-httpx_2.11" % "1.3.3",
  "com.github.tminglei" %% "slick-pg" % "0.14.2",
  "com.github.tminglei" %% "slick-pg_date2" % "0.14.2"
)