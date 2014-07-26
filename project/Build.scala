
import sbt._

object Build extends sbt.Build {
  lazy val root = (Project(id = "rogerfs", base = file("."))
    aggregate (core, common, test)
    )

  lazy val core = (Project(id = "core", base = file("rogerfs-core"))
    dependsOn (common, test % "test->compile")
    )

  lazy val test = (Project(id = "test", base = file("rogerfs-test"))
    dependsOn common
    )

  lazy val common = Project(id = "common", base = file("rogerfs-common"))
}
