package com.wlangiewicz.cctt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.wlangiewicz.cctt.config.ApplicationConfig
import com.wlangiewicz.cctt.core.{ExchangeSync, ExchangeWrapper}
import com.wlangiewicz.cctt.http.Routes
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main extends App with LazyLogging {
  implicit private lazy val system: ActorSystem = ActorSystem("cctt")

  val config = ApplicationConfig.Config

  val exchange = new ExchangeWrapper(config.exchange, config.key, config.secret)

  val exchangeSync = new ExchangeSync(exchange, config.pair)

  val tradeService = exchange.tradeService

  val runner = new Runner(config, tradeService, exchangeSync)

  val routes = new Routes(exchange.accountService, exchange.tradeService)

  val host = "0.0.0.0"
  val port = 8080
  val bindingFuture = Http().newServerAt(host, port).bind(routes.route)

  bindingFuture.onComplete {
    case Success(_) => logger.info(s"CCTT online at $host:$port")
    case Failure(_) => logger.error(s"Unable to start CCTT on $host:$port")
  }

  scala.sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info(s"Shutting down ... ")
        system.terminate()
      }
  }

  runner.loop(config.sleep)
}
