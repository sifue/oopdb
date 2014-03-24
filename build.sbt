name := "oopdb"

version := "1.0"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.175",
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)