package net.atos.carers.web.endpoint

import scala.xml.Elem
import scala.xml.XML
import org.cddcore.carers.Carers
import org.cddcore.carers.CarersXmlSituation
import org.cddcore.carers.Claim
import org.cddcore.carers.DateRange
import org.cddcore.carers.NinoToCis
import org.cddcore.carers.TimeLineCalcs
import org.cddcore.carers.TimeLineItem
import org.cddcore.carers.World
import org.cddcore.engine.ClassFunction
import org.cddcore.engine.Engine
import org.cddcore.engine.HtmlRenderer
import org.cddcore.engine.LoggerDisplayProcessor
import org.cddcore.engine.Report
import org.cddcore.engine.SimpleReportableToUrl
import org.cddcore.engine.TraceItem
import org.cddcore.engine._
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.joda.time.DateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import scala.collection.immutable.Iterable

class ClaimHandler(ninoToCis: NinoToCis) extends AbstractHandler {
  private val MethodPost: String = "POST";

  private val MethodGet: String = "GET";
  val world = World(ninoToCis)
  val contentTypeText = "text/html;charset=utf-8"
  val contentTypeJson = "application/json;charset=utf-8"

  def print(ldp: LoggerDisplayProcessor = LoggerDisplayProcessor())(item: TraceItem): String =
    TraceItem.fold[String]("",
      (acc: String, depth: Int, i: TraceItem) => acc + f"\n ${i.took}%12d ${Strings.blanks(depth)} ${i.engine.titleString} ( ${i.params.map(ldp(_)).mkString(",") + ") => " + ldp(i.result)}",
      (acc: String, depth: Int, i: TraceItem) => acc,
      item)

  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    response.setStatus(HttpServletResponse.SC_OK);
    try {
      val path = request.getRequestURI()
      val (body, contentType) = (request.getMethod, path) match {
        case (MethodPost, "/json") => (getJson(request.getParameter("custxml")), contentTypeJson)
        case (MethodPost, "/tracesummary") => {
          val (_, tr) = Engine.trace(handlePost(request.getParameter("custxml"), request.getParameter("claimDate")))
          val trace = tr.map((x) => print(loggerDisplayProcessor)(x).replaceAll(" ", "&nbsp;")).mkString("\n")
          val html = trace.replaceAll("\n", "<br />")
          (html, contentTypeText)
        }
        case (MethodPost, "/tracehtml") => {
          val (_, tr) = Engine.trace(handlePost(request.getParameter("custxml"), request.getParameter("claimDate")))
          val report = Report("Trace", tr: _*)
          val reportableToUrl = new SimpleReportableToUrl
          val urlMap = reportableToUrl.makeUrlMap(report)
          val trace = HtmlRenderer.apply(loggerDisplayProcessor, false).traceHtml(Some("/")).render(reportableToUrl, urlMap, report)
          (trace, contentTypeText)
        }
        case (MethodPost, _) => (handlePost(request.getParameter("custxml"), request.getParameter("claimDate")), contentTypeText)
        case (MethodGet, _) => (handleGet, contentTypeText)
      }
      response.setContentType(contentType)
      response.getWriter().println(body)
      Runtime.getRuntime().gc()
      Runtime.getRuntime().gc()
      println("Free nemory: " + Runtime.getRuntime().freeMemory())
    } catch {
      case e: Throwable =>
        e.printStackTrace(response.getWriter);
        e.printStackTrace()
        println()
        e.getCause().printStackTrace()
        val props = System.getProperties();
        props.list(System.err);
        props.list(response.getWriter());
        response.getWriter().println("Class Path = ")
        response.getWriter().println(props.get("java.class.path"))

    }
    baseRequest.setHandled(true)
  }

  def getJson(custXml: String) = {
    val xml = try { XML.loadString(custXml) } catch { case e: Throwable => e.printStackTrace(); throw e }
    val situation = CarersXmlSituation(world, xml)
    val timeLine: List[TimeLineItem] = TimeLineCalcs.foldTimelineOnItemKeys(TimeLineCalcs.findTimeLine(situation))
    TimeLineCalcs.toJson(timeLine)
  }

  def handleGet() = getCarerView("", getDefaultDate);
  val loggerDisplayProcessor = LoggerDisplayProcessor(
    ClassFunction(classOf[CarersXmlSituation], (ldp: LoggerDisplayProcessor, c: CarersXmlSituation) => "CarersXmlSituation"),
    ClassFunction(classOf[List[_]], (ldp: LoggerDisplayProcessor, l: List[_]) => "List(" + l.map(ldp(_)).mkString(",") + ")"),
    ClassFunction(classOf[(DateRange, String)], (ldp: LoggerDisplayProcessor, ds: (DateRange, String)) => "(" + ldp(ds._1) + ",\"" + ds._2 + "\")"),
    ClassFunction(classOf[DateTime], (ldp: LoggerDisplayProcessor, d: DateTime) => DateRange.formatter.print(d)))

  def handlePost(custXml: String, claimDate: String) = {
    println("In handle post 1 ")

    val start = System.nanoTime()
    val xml = try { XML.loadString(custXml) } catch { case e: Throwable => e.printStackTrace(); throw e }
    val xmlTime = System.nanoTime() - start
    val situation = CarersXmlSituation(world, xml)
    val situationTime = System.nanoTime() - start - xmlTime

    val (result, pr) = Engine.profile(claimDate.isEmpty() match {
      case true => {
        val timeLine: List[TimeLineItem] = TimeLineCalcs.foldTimelineOnItemKeys(TimeLineCalcs.findTimeLine(situation))
        println("empty date")
        <div id='timeLine'>{
          timeLine.map((tli) => <p>{ tli }</p>)
        }</div>
      }
      case _ => {
        println("not empty date")
        val dateTime = Claim.asDate(claimDate)
        val result = Carers.engine(dateTime, situation)
        <div id='oneTime'>{ result }</div>
      }
    })
    println("In handle post 2: " + result)
    val string = pr.results.map { case (e, r) => f"${r.totalTime / 1000.0}%,10.2fus ${r.count}%3d ${r.totalTime / r.count / 1000.0}%,10.2fus ${pr.prettifyFn(e)}" }.toList.sortBy(_.toString).mkString("\n")
    println(string)

    //    //CDD Business logic will return a return message - hard coded for now   
    //    val returnMessage = result.toString

    val calcTime = System.nanoTime() - xmlTime - situationTime - start
    def printit(title: String, duration: Long) = println(f"${title}%15s ${duration / 1000.0}%,10.2fus")
    println
    printit("Xml Parsing", xmlTime)
    printit("SituationTime", situationTime)
    printit("Calc time", calcTime)
    printit("Total", System.nanoTime() - start)
    getCarerView(custXml, claimDate, result)
  }

  val operatingPort = System.getenv("PORT")

  def getCarerView(xmlString: String, claimDate: String, returnMessage: Elem = <nothing/>): Elem =
    <html>
      <head>
        <title>Validate Claim</title>
      </head>
      <body>
        <form action="/" method="POST">
          <h1>Validate Claim</h1>
          <table>
            <tr>
              <td>
                Claim Xml:
              </td>
              <td>
                <textarea name="custxml">{ xmlString }</textarea>
              </td>
            </tr>
            <tr>
              <td>
                Claim Date:
              </td>
              <td>
                <input type="text" name="claimDate" value={ claimDate }/>
              </td>
              <td>
                <input id="submit" type="submit" value="Submit"/>
              </td>
            </tr>
          </table>
          <br/>
          <pre>{ returnMessage }</pre>
        </form>
        <form action="/json" method="POST">
          <h1>Validate Claim Json</h1>
          <table>
            <tr>
              <td>
                Claim Xml:
              </td>
              <td>
                <textarea name="custxml">{ xmlString }</textarea>
              </td>
              <td>
                <input id="submitjson" type="submit" value="Submit"/>
              </td>
            </tr>
          </table>
        </form>
        <form action="/tracesummary" method="POST">
          <h1>Trace Summary</h1>
          <table>
            <tr>
              <td>
                Claim Xml:
              </td>
              <td>
                <textarea name="custxml">{ xmlString }</textarea>
              </td>
            </tr>
            <tr>
              <td>
                Claim Date:
              </td>
              <td>
                <input type="text" name="claimDate" value={ claimDate }/>
              </td>
              <td>
                <input id="submitTraceSummary" type="submit" value="Submit"/>
              </td>
            </tr>
          </table>
        </form>
        <form action="/tracehtml" method="POST">
          <h1>Trace Html</h1>
          <table>
            <tr>
              <td>
                Claim Xml:
              </td>
              <td>
                <textarea name="custxml">{ xmlString }</textarea>
              </td>
            </tr>
            <tr>
              <td>
                Claim Date:
              </td>
              <td>
                <input type="text" name="claimDate" value={ claimDate }/>
              </td>
              <td>
                <input id="submitTraceHtml" type="submit" value="Submit"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>

  def getDefaultDate: String = {
    "2010-07-25"
  }
}