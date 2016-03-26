package com.akkademy

import akka.actor.Actor
import com.akkademy.ArticleParser.{ArticleBody, ParseHtmlArticle}

/**
  * Created by Travis on 3/26/2016.
  */
class ParsingActor extends Actor {
  override def receive: Receive = {
    case ParseHtmlArticle(key, html) =>
      sender() ! ArticleBody(key, de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(html))
    case x =>
      println("unknown message " + x.getClass)
  }
}
