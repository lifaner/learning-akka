package com.akkademy.messages.com.akkademy

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.akkademy.messages.SetRequest
import org.scalatest.{FunSpecLike, Matchers}

/**
  * Created by Travis on 3/22/2016.
  */
class AkkademyDbSpec extends FunSpecLike with Matchers {
  implicit val system = ActorSystem()

  describe("akkademyDb") {
    describe("given SetRequest") {
      it("should place key/value into map") {
        val actorRef = TestActorRef(new AkkademyDb)
        actorRef ! SetRequest("key", "value")
        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
      }
    }
  }
}
