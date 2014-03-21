package net.atos.cis.web.endpoint

import org.eclipse.jetty.server.Server

object CisEndpoint {
  val cisHandler = new CISHandler

  def defaultPort = {
    val portString = System.getenv("PORT")
    println("PortString[" + portString + "]")
    val port = portString match { case null => 8091; case _ => portString.toInt }
    println("Port[" + port + "]")
    port
  }

  def main(args: Array[String]) {
    val s = new Server(defaultPort);
    s.setHandler(cisHandler);
    s.start
    s.join
  }
}