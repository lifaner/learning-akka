package com.akkademy

import akka.actor.{Actor, ActorSystem, Props, Status}
import akka.event.Logging
import com.akkademy.messages.{GetRequest, KeyNotFoundException, SetRequest}

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
      sender() ! Status.Success
    case GetRequest(key) =>
      log.info(s"Received GetRequest - key: $key")
      map.get(key) match {
        case Some(value) => sender() ! value
        case None => sender() ! Status.Failure(new KeyNotFoundException(key))
      }
    case o => Status.Failure(new ClassNotFoundException)
  }
}

object Main extends App {
  val system = ActorSystem("akkademy")
  system.actorOf(Props[AkkademyDb], name = "akkademy-db")
}
