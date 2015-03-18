package controllers

import scala.concurrent._
import duration._
import org.specs2.mutable._

import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._
import java.util.concurrent.TimeUnit


/**
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class VesselsIT extends Specification {

  val timeout: FiniteDuration = FiniteDuration(5, TimeUnit.SECONDS)

  "Users" should {

    "insert a valid json" in {
      running(FakeApplication()) {
        val request = FakeRequest.apply(POST, "/vessel").withJsonBody(Json.obj(
          "name" -> "Titanic",
          "width" -> 20,
          "length" -> 60,
          "draft" -> 10,
          "longitude" -> 11.00,
          "latitude" -> 90.00))
        val response = route(request)
        response.isDefined mustEqual true
        val result = Await.result(response.get, timeout)
        result.header.status must equalTo(CREATED)
      }
    }

    "fail inserting a non valid json" in {
      running(FakeApplication()) {
        val request = FakeRequest.apply(POST, "/vessel").withJsonBody(Json.obj(
          "name" -> 1234,
          "width" -> 11,
          "length" -> 27,
          "draft" -> 10,
          "longitude" -> 11.00,
          "latitude" -> 90.00))
        val response = route(request)
        response.isDefined mustEqual true
        val result = Await.result(response.get, timeout)
        contentAsString(response.get) mustEqual "Invalid JSON"
        result.header.status mustEqual BAD_REQUEST
      }
    }

    "update a valid json" in {
      running(FakeApplication()) {
        val request = FakeRequest.apply(PUT, "/vessel/Titanic").withJsonBody(Json.obj(
          "name" -> "Titanic",
          "width" -> 20,
          "length" -> 60,
          "draft" -> 10,
          "longitude" -> 11.00,
          "latitude" -> 90.00))
        val response = route(request)
        response.isDefined mustEqual true
        val result = Await.result(response.get, timeout)
        result.header.status must equalTo(OK)
      }
    }

    "fail updating a non valid json" in {
      running(FakeApplication()) {
        val request = FakeRequest.apply(PUT, "/vessel/Titanic").withJsonBody(Json.obj(
          "name" -> "Titanic",
          "width" -> 20,
          "length" -> 60,
          "draft" -> 10,
          "longitude" -> 11.00))
        val response = route(request)
        response.isDefined mustEqual true
        val result = Await.result(response.get, timeout)
        contentAsString(response.get) mustEqual "Invalid JSON"
        result.header.status mustEqual BAD_REQUEST
      }
    }
  }
}