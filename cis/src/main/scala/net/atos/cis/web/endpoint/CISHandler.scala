package net.atos.cis.web.endpoint

import scala.xml.Elem

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import net.atos.cis.Cis

class CISHandler extends AbstractHandler {
  private val MethodPost: String = "POST"
  private val MethodGet: String = "GET"
  private val PageTitle: String = "CIS Details"

  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    request.getMethod match {
      case MethodPost => {
        response.setContentType("application/xml;charset=utf-8")
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(handlePost(request.getParameter("custId")))
      }
      case MethodGet => {
        response.setStatus(HttpServletResponse.SC_OK);
        val url = request.getRequestURI()
        url match {
          case "/" => {
            response.setContentType("text/html;charset=utf-8")
            response.getWriter().println(this.handlePageGet)
          }
          case _ => {
            response.setContentType("application/xml;charset=utf-8")
            response.getWriter().println(this.handleServiceGet(url.substring(1)))
          }
        }
      }
      case _ => {
        response.setContentType("text/html;charset=utf-8")
        response.getWriter().println(handleInvalidRequest)
      }
    }

    baseRequest.setHandled(true)
  }

  def handleInvalidRequest = {
    getCisView("Invalid Http Request Type")
  }

  def handlePageGet = {
    getCisView("")
  }

  def handleServiceGet(nino: String) = {
    Cis().ni2PersonDetails(nino)
  }

  def handlePost(nino: String) = {
    Cis().ni2PersonDetails(nino)
  }

  def getCisView(message: String): Elem =
    <html>
      <head>
        <title>{ PageTitle }</title>
      </head>
      <body>
        <h1>{ PageTitle }</h1>
        <form action="/" method="POST">
          <table>
            <tr>
              <td>
                <input type="text" name="custId"></input>
              </td>
              <td>
                <input type="submit" value="Submit"/>
              </td>
            </tr>
          </table>
        </form>
        { if (message.length() > 0) <b>{ message }</b> }
      </body>
    </html>
}