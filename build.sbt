name := "rogerfs"

version in Global := "0.1.0"

scalaVersion in Global := "2.11.6"

libraryDependencies in Global += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"

libraryDependencies in Global += "com.typesafe" % "config" % "1.3.0"

mainClass in (Compile, run) := Some("org.rogerfs.shell.RogerFsShellApp")

