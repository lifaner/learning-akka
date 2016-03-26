package com.akkademy

import java.util.concurrent.TimeoutException

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import com.akkademy.ArticleParser.{ArticleBody, HttpResponse, ParseArticle, ParseHtmlArticle}
import com.akkademy.messages.{GetRequest, SetRequest}

/**
  * Created by Travis on 3/26/2016.
  */
class ArticleParser(cacheActorPath: String, httpClientActorPath: String, articleParserActorPath: String,
                    implicit val timeout: Timeout) extends Actor {
  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(articleParserActorPath)

  implicit val ex = context.dispatcher

  override def receive: Receive = {
    case msg @ ParseArticle(uri) =>
      val extraActor = buildExtraActor(sender(), uri)

      cacheActor.tell(GetRequest(uri), extraActor)
      httpClientActor.tell("test", extraActor)

      context.system.scheduler.scheduleOnce(timeout.duration, extraActor, "timeout")
  }

  private def buildExtraActor(senderRef: ActorRef, uri: String): ActorRef = {
    context.actorOf(Props(new Actor {
      override def receive: Receive = {
        case "timeout" =>
          senderRef ! Failure(new TimeoutException("timeout!"))
          context.stop(self)
        case HttpResponse(body) =>
          articleParserActor ! ParseHtmlArticle(uri, body)
        case body: String =>
          senderRef ! body
          context.stop(self)
        case ArticleBody(_, body) =>
          cacheActor ! SetRequest(uri, body)
          senderRef ! body
          context.stop(self)
        case t =>
          println("ignoring msg: " + t.getClass)
      }
    }))
  }
}

object ArticleParser {
  case class ParseArticle(uri: String)
  case class HttpResponse(body: String)
  case class ParseHtmlArticle(uri: String, body: String)
  case class ArticleBody(uri: String, body: String)
}
