package com.wlangiewicz.cctt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.config.ApplicationConfig
import com.wlangiewicz.cctt.core.ExchangeIoBuilder
import com.wlangiewicz.cctt.http.docs.DocsRoute
import com.wlangiewicz.cctt.http.endpoints.gettrade.{GetTradeController, GetTradeRoute}
import com.wlangiewicz.cctt.http.endpoints.newtrade.{NewTradeController, NewTradeRoute}
import com.wlangiewicz.cctt.http.endpoints.openorders.{OpenOrdersController, OpenOrdersRoute}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main extends App with LazyLogging {
  implicit private lazy val system: ActorSystem = ActorSystem("cctt")

  val config = ApplicationConfig.Config

  val exchangeIo = ExchangeIoBuilder.build(config.exchange, config.key, config.secret)

  val runner = new Runner(exchangeIo)

  val docsRoute = new DocsRoute
  val openOrdersRoute = new OpenOrdersRoute(new OpenOrdersController)
  val newTradeRoute = new NewTradeRoute(new NewTradeController)
  val getTradeRoute = new GetTradeRoute(new GetTradeController)

  val routes = Route.seal(
    docsRoute.route ~
      openOrdersRoute.route ~ newTradeRoute.route ~ getTradeRoute.route
  )

  val host = "0.0.0.0"
  val port = 8080
  val bindingFuture = Http().newServerAt(host, port).bind(routes)

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
