package com.akkademy

import akka.actor.Status.Failure
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.TestProbe
import akka.util.Timeout
import com.akkademy.ArticleParser.{HttpResponse, ParseArticle}
import com.akkademy.messages.{GetRequest, SetRequest}

import scala.concurrent.duration._
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Await

/**
  * Created by Travis on 3/26/2016.
  */
class ArticleParserTest extends FunSpec with Matchers {
  implicit val system = ActorSystem("test")
  implicit val timeout = Timeout(10 seconds)

  val cacheProbe = TestProbe()
  val httpClientProbe = TestProbe()
  val parsingActor = system.actorOf(Props[ParsingActor])
  val articleParserActor = system.actorOf(
    Props(classOf[ArticleParser],
      cacheProbe.ref.path.toString,
      httpClientProbe.ref.path.toString,
      parsingActor.path.toString,
      timeout)
  )

  describe("Article Parser") {
    it("should provide parsed article") {
      val f = articleParserActor ? ParseArticle("http://www.google.com")

      cacheProbe.expectMsgType[GetRequest]
      cacheProbe.reply(Failure(new Exception("no cache")))

      httpClientProbe.expectMsgType[String]
      httpClientProbe.reply(HttpResponse(Articles.article1))

      cacheProbe.expectMsgType[SetRequest]

      val parsedArticle = Await.result(f, 10 seconds)
      parsedArticle.toString should include("I’ve been writing a lot in emacs lately")
      parsedArticle.toString should not include "<body>"
    }

    it("should provide cached article") {
      val f = articleParserActor ? ParseArticle("http://www.google.com")

      cacheProbe.expectMsgType[GetRequest]
      cacheProbe.reply("Test text.")

      val parsedArticle = Await.result(f, 10 seconds)
      parsedArticle.toString should equal("Test text.")
    }
  }
}
