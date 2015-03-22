package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import play.api.mvc._
import play.api.libs.json._

/**
 * The Vessels controllers encapsulates the REST endpoints and the interaction with the MongoDB, via ReactiveMongo
 * Play plugin. This provides a non-blocking driver for MongoDB as well as some useful additions for handling JSON.
 * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */
class Vessels extends Controller with MongoController {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Vessels])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection]("vessels")

  import models._

  def createVessel = Action.async(parse.json) {
    request =>
    /*
     * request.body is a JsValue.
     * There is an implicit Writes that turns this JsValue as a JsObject,
     * so you can call insert() with this JsValue.
     * (insert() takes a JsObject as parameter, or anything that can be
     * turned into a JsObject using a Writes.)
     */
      request.body.validate[Vessel].map {
        vessel =>
          collection.insert(vessel).map {
            lastError =>
              logger.debug(s"Successfully inserted with LastError: $lastError")
              Created(s"Vessel Created")
          }
      }.recoverTotal { error =>
        Future.successful(BadRequest("Invalid JSON: " + JsError.toFlatJson(error)))
      }
  }

  def updateVessel(name: String) = Action.async(parse.json) {
    request =>
      request.body.validate[Vessel].map {
        vessel =>
          // Find our vessel by name
          val nameSelector = Json.obj("name" -> name)
          collection.update(nameSelector, vessel).map {
            lastError =>
              logger.debug(s"Successfully updated with LastError: $lastError")
              Ok(s"Vessel Updated")
          }
      }.recoverTotal { error =>
        Future.successful(BadRequest("Invalid JSON: " + JsError.toFlatJson(error)))
      }
  }

  def deleteVessel(name: String) = Action.async {
    collection.remove(Json.obj("name" -> name)).map {
      lastError =>
        logger.debug(s"Successfully deleted with LastError: $lastError")
        Ok(s"Vessel Deleted")
    }
  }

  def findVessels = Action.async {
    val cursor: Cursor[Vessel] = collection.
      // Find all
      find(Json.obj()).
      // Sort them by creation date
      sort(Json.obj("created" -> -1)).
      // Perform the query and get a cursor of JsObject
      cursor[Vessel]

    // Gather all the JsObjects in a list
    val futureVesselsList: Future[List[Vessel]] = cursor.collect[List]()

    // Transform the list into a JsArray
    val futureVesselsJsonArray: Future[JsArray] = futureVesselsList.map { vessels =>
      Json.arr(vessels)
    }
    // Everything is OK! Let's reply with the array
    futureVesselsJsonArray.map {
      vessels =>
        Ok(vessels(0))
    }
  }
}
