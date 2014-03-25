package org.cddcore.carers

import scala.xml.Elem
import java.net.URL
import scala.io.Source
import scala.xml.XML

trait NinoToCis {
  def apply(nino: String): Elem
}

class TestNinoToCis extends NinoToCis {
  def apply(nino: String) =
    try {
      val full = s"Cis/${nino}.txt"
      val url = getClass.getClassLoader.getResource(full)
      if (url == null)
        <NoCis/>
      else {
        val xmlString = scala.io.Source.fromURL(url).mkString
        val xml = XML.loadString(xmlString)
        xml
      }
    } catch {
      case e: Exception => throw new RuntimeException("Cannot load " + nino, e)
    }
}
class WebserverNinoToCis(host: String) extends NinoToCis {
  def apply(nino: String): Elem = {
    val s = Source.fromURL(host + nino).mkString
    XML.loadString(s)
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