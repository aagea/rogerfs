
/*
 * Copyright (c) 2014 Alvaro Agea.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import scoverage.ScoverageSbtPlugin._

object Build extends sbt.Build {
  lazy val root = (Project(id = "rogerfs", base = file("."))
    aggregate (core, common, test)
    settings (instrumentSettings: _*)
    )

  lazy val core = (Project(id = "core", base = file("rogerfs-core"))
    dependsOn (common, test % "test->compile")
    settings (instrumentSettings: _*)
    )

  lazy val test = (Project(id = "test", base = file("rogerfs-test"))
    dependsOn common
    settings (instrumentSettings: _*)
    )

  lazy val common = (Project(id = "common", base = file("rogerfs-common"))
    settings (instrumentSettings: _*)
    )
  lazy val cassandraDriver= (Project(id="cassandra-store", base = file("rogerfs-cassandra-store"))
    dependsOn(common)
    settings(instrumentSettings: _*))
}
