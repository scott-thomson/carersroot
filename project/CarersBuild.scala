import sbt._
import Keys._

object CarersBuild extends Build {
  lazy val cis = Project(id = "cis", base = file("cis"))
  lazy val carers = Project(id = "carers", base = file("carers")).dependsOn(cis)
  lazy val frontend = Project(id = "frontend", base = file("frontend")).dependsOn( cis)
  lazy val carersroot = Project(id = "carersroot", base = file(".")).aggregate(carers, frontend, cis)
}