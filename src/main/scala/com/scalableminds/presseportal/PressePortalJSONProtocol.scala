package com.scalableminds.presseportal

import org.joda.time.DateTime

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait APIResult { val success: Boolean }
case class APISuccess(success: Boolean, request: RequestInfo, content: List[Article]) extends APIResult
case class APIFailure(success: Boolean, error: APIError) extends APIResult
case class APIError(code: String, msg: String)
case class RequestInfo(uri: String, start: Int, limit: Int, format: String)

case class Article(id: String,
  url: String,
  title: String,
  body: String,
  published: DateTime,
  language: String,
  ressort: String,
  company: Company,
  keywords: List[String])

case class Company(id: String, name: String)
case class KeyWordObject(keyword: List[String])

object PressePortalJsonProtocol {
  def statusReads(implicit r: Reads[String]): Reads[Boolean] = r.map(status => if (status == "1") true else false)
  def stringToIntReads(implicit r: Reads[String]): Reads[Int] = r.map(s => s.toInt)
  def stringToDateReads(implicit r: Reads[String]): Reads[DateTime] = r.map(s => DateTime.parse(s))
  def emptyStringToEmptyList(implicit r: Reads[String]): Reads[List[String]] = r.map(s => List[String]())
  def flattenKeyWordObject(implicit r: Reads[KeyWordObject]): Reads[List[String]] = r.map(kwo => kwo.keyword)
  
  implicit val keyWordObjectReads: Reads[KeyWordObject] = Json.reads[KeyWordObject]
  implicit val companyReads: Reads[Company] = Json.reads[Company]
  implicit val apiErrorReads: Reads[APIError] = Json.reads[APIError]
  implicit val articleReads = (
    (__ \ "id").read[String] and
    (__ \ "url").read[String] and
    (__ \ "title").read[String] and
    (__ \ "body").read[String] and
    (__ \ "published").read(stringToDateReads) and
    (__ \ "language").read[String] and
    (__ \ "ressort").read[String] and
    (__ \ "company").read[Company] and
    (__ \ "keywords").read(flattenKeyWordObject or emptyStringToEmptyList))(Article)

  implicit val requestInfoReads = (
    (__ \ "uri").read[String] and
    (__ \ "start").read(stringToIntReads) and
    (__ \ "limit").read(stringToIntReads) and
    (__ \ "format").read[String])(RequestInfo)

  val apiSuccessReads: Reads[APIResult] = (
    (__ \ "success").read(statusReads) and
    (__ \ "request").read[RequestInfo] and
    (__ \ "content" \ "story").read[List[Article]])(APISuccess)

  val apiFailureReads: Reads[APIResult] = (
    (__ \ "success").read(statusReads) and
    (__ \ "error").read[APIError])(APIFailure)

  implicit val apiResultReads: Reads[APIResult] = (
    (__).read(apiSuccessReads or apiFailureReads))
}

object PressePortalProperJsonFormat {
  implicit val companyFormat: Format[Company] = Json.format[Company]
  implicit val articleFormat: Format[Article] = Json.format[Article]
}
