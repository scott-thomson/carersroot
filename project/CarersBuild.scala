import sbt._
import Keys._

object CarersBuild extends Build {
  lazy val cis = Project(id = "cis", base = file("cis"))
  lazy val carersengine = Project(id = "carersengine", base = file("carersengine")) .dependsOn( cis)
  lazy val carers = Project(id = "carers", base = file("carers")).dependsOn(carersengine)
  lazy val frontend = Project(id = "frontend", base = file("frontend")).dependsOn(carersengine).aggregate( cis)
  lazy val carersroot = Project(id = "carersroot", base = file(".")).aggregate(carers, frontend, cis, carersengine)
}