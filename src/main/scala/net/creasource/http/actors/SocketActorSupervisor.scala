package net.creasource.http.actors

import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import spray.json.JsonParser.ParsingException

object SocketActorSupervisor {
  def props(): Props = Props(new SocketActorSupervisor)
}

class SocketActorSupervisor extends Actor {

  private val logger = Logging(context.system, this)

  override def receive: Receive = {
    case props: Props => sender() ! context.actorOf(props)
  }

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(loggingEnabled = true) {
      case p: ParsingException =>
        logger.error(p, "Sent message was not a correct json message. Resuming.")
        SupervisorStrategy.Resume
      case _: Exception => SupervisorStrategy.Stop
    }
}
