import sbt._
import Keys._

object CarersBuild extends Build {
  lazy val carersroot = Project(id="carersroot", base = file(".")).aggregate(carers, carersengine)

  lazy val carers = Project(id = "carers", base = file("carers")).dependsOn(carersengine)
  
//  lazy val frontend = Project(id = "frontend", base = file("frontend")).dependsOn(carersengine)

  lazy val carersengine = Project(id = "carersengine", base = file("carersengine"))
}