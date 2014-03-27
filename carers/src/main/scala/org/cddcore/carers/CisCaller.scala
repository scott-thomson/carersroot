package org.cddcore.carers

import scala.xml.Elem
import java.net.URL
import scala.io.Source
import scala.xml.XML

class WebserverNinoToCis(host: String) extends NinoToCis {
  def apply(nino: String): Elem = {
    val start = System.nanoTime()
    val s = Source.fromURL(host + nino).mkString
    val getItTime = (System.nanoTime() - start) /1000.0
    
    
    val result = XML.loadString(s)
    val duration = (System.nanoTime() - start) / 1000.0
    println(f"Get it  took ${getItTime}%,5.2fus")
    println(f"Plus parsing took ${duration}%,5.2fus")
    result
  }
}

object WebserverNinoToCis {
  val cisHost = "http://localhost:8091/"
  def apply(host: String = cisHost): NinoToCis = new WebserverNinoToCis(host)

  //  def main(args: Array[String]) {
  //    val ninoToCis = WebserverNinoToCis("http://atos-cis.pcfapps.vsel-canopy.com/")
  //    println(ninoToCis("CL100104A"))
  //  }
}