package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Vessel(name: String, width: Float, length: Float, draft: Float, latitude: Float, longitude: Float)

object Vessel {

  implicit val vesselFormat: Format[Vessel] = (
    (__ \ "name").format[String] and
    (__ \ "length").format[Float](min(1.0F)) and
    (__ \ "width").format[Float](min(1.0F)) and
    (__ \ "draft").format[Float](min(1.0F)) and
    (__ \ "longitude").format[Float](min(-180.0F) keepAnd max(180.0F)) and
    (__ \ "latitude").format[Float](min(-90.0F) keepAnd max(90.0F))
  )(Vessel.apply, unlift(Vessel.unapply))
}