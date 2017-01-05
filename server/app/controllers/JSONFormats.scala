package controllers

import play.api.libs.json._
import shared.models.Point

trait JSONFormats {
  private val defaultPointFormat = Json.format[Point]

  implicit val pointFormat = new Format[Point] {
    def reads(json: JsValue): JsResult[Point] =
      (json \ "event").validate[String].flatMap {
        case "point" =>
          defaultPointFormat.reads(json)
        case _ =>
          JsError(JsPath \ "event", s"Event is not `point`")
      }

    def writes(o: Point): JsValue =
      Json.obj("event" -> o.event) ++
        defaultPointFormat.writes(o).as[JsObject]
  }
}
