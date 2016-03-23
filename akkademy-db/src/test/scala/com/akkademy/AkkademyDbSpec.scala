package com.akkademy

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout

import scala.concurrent.duration._
import com.akkademy.messages.{GetRequest, KeyNotFoundResponse, SetRequest}
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.Await

/**
  * Created by Travis on 3/22/2016.
  */
class AkkademyDbSpec extends FunSpecLike with Matchers {
  implicit val system = ActorSystem()
  implicit val timeout = new Timeout(1 second)

  describe("akkademyDb") {
    describe("given SetRequest") {
      it("should place key/value into map") {
        val actorRef = TestActorRef(new AkkademyDb)
        actorRef ! SetRequest("key", "value")
        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
      }
    }
    describe("given GetRequest") {
      describe("given key exists") {
        it("should return the correct value") {
          val actorRef = TestActorRef(new AkkademyDb)
          actorRef.underlyingActor.map.put("key", "value")

          val valueFuture = actorRef ? GetRequest("key")
          val value = Await.result(valueFuture, 1 second).asInstanceOf[String]

          value should equal("value")
        }
      }
      describe("given key doesn't exist") {
        it("should return KeyNotFoundResponse") {
          val actorRef = TestActorRef(new AkkademyDb)
          intercept[KeyNotFoundResponse] {
            Await.result(actorRef ? GetRequest("key"), 1 second).asInstanceOf[String]
          }
        }
      }
    }
  }
}
