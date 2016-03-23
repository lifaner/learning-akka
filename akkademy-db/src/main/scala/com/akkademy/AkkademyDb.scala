package com.akkademy

import akka.actor.{Actor, Status}
import akka.event.Logging
import com.akkademy.messages.{GetRequest, KeyNotFoundResponse, SetRequest}

import scala.collection.mutable

/**
  * Created by Travis on 3/22/2016.
  */
class AkkademyDb extends Actor {
  val map = new mutable.HashMap[String, Object]
  val log = Logging(context.system, this)

  override def receive = {
    case SetRequest(key, value) =>
      log.info(s"Received SetRequest - key: $key, value: $value")
      map.put(key, value)
    case GetRequest(key) =>
      map.get(key) match {
        case Some(value) => sender() ! value
        case _ => sender() ! Status.Failure(new KeyNotFoundResponse)
      }
    case o => log.info(s"Received unknown message: $o")
  }
}
