package models

case class Vessel(name: String, width: Float, length: Float, draft: Float, latitude: Float, longitude: Float)

object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and Vessel thanks to Json Macros
  implicit val vesselFormat = Json.format[Vessel]
}