package com.akkademy

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout

import scala.concurrent.duration._
import com.akkademy.messages._
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
          intercept[KeyNotFoundException] {
            Await.result(actorRef ? GetRequest("key"), 1 second).asInstanceOf[String]
          }
        }
      }
    }
    describe("given SetIfNotExists") {
      describe("given key does not exist") {
        it("should place key/value into map") {
          val actorRef = TestActorRef(new AkkademyDb)
          actorRef ! SetIfNotExists("key", "value")
          val akkademyDb = actorRef.underlyingActor
          akkademyDb.map.get("key") should equal(Some("value"))
        }
      }
      describe("given key exists") {
        it("should not replace existing value") {
          val actorRef = TestActorRef(new AkkademyDb)
          actorRef.underlyingActor.map.put("key", "value")
          actorRef ! SetIfNotExists("key", "another value")
          val akkademyDb = actorRef.underlyingActor
          akkademyDb.map.get("key") should equal(Some("value"))
        }
      }
    }
    describe("given Delete") {
      describe("give key exists") {
        it("should remove key/value pair") {
          val actorRef = TestActorRef(new AkkademyDb)
          actorRef.underlyingActor.map.put("key", "value")
          actorRef ! Delete("key")

          intercept[KeyNotFoundException] {
            Await.result(actorRef ? GetRequest("key"), 1 second).asInstanceOf[String]
          }
        }
      }
      describe("given key does not exist") {
        it("should not fail") {
          val actorRef = TestActorRef(new AkkademyDb)
          actorRef ! Delete("key")
        }
      }
    }
  }
}
