package com.scalableminds.presseportal

import scala.concurrent._
import scala.concurrent.duration._

import spray.httpx.PlayJsonSupport._
import spray.client.pipelining._
import spray.can.Http

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

class PressePortal(APIKey: String) {
  implicit val system = ActorSystem()
  import ExecutionContext.Implicits.global // execution context for futures
  import PressePortalJsonProtocol._

  val entryPoint = "http://api.presseportal.de/api"

  val pipeline = sendReceive ~> unmarshal[APIResult]

  def find(start: Int = 0, limit: Int = 20) = basicRequest("/article/all", start, limit)

  def findByDepartment(department: String, start: Int = 0, limit: Int = 20) =
    /* for possible values of department see http://api.presseportal.de/doc/value/ressort */
    basicRequest(s"/article/ressort/$department", start, limit)

  def findBySector(sector: String, start: Int = 0, limit: Int = 20) =
    /* for possible values of sector see http://api.presseportal.de/doc/value/branche */
    basicRequest(s"/article/branche/$sector", start, limit)

  def basicRequest(path: String, start: Int = 0, limit: Int = 20): Future[APIResult] = {
    pipeline {
      Get(s"${entryPoint}${path}?api_key=${APIKey}&format=json&start=$start&limit=$limit")
    }
  }

  def shutDown() = {
    implicit val timeout = Timeout(2 seconds)
    Await.ready(IO(Http).ask(Http.CloseAll), 2 seconds)
    system.shutdown()
  }
}