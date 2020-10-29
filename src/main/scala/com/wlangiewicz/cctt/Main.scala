package com.wlangiewicz.cctt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.config.ApplicationConfig
import com.wlangiewicz.cctt.core.{ExchangeIoBuilder, ExchangeSync}
import com.wlangiewicz.cctt.http.docs.DocsRoute
import com.wlangiewicz.cctt.http.Routes
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Directives._

object Main extends App with LazyLogging {
  implicit private lazy val system: ActorSystem = ActorSystem("cctt")

  val config = ApplicationConfig.Config

  val exchangeIo = ExchangeIoBuilder.build(config.exchange, config.key, config.secret)

  val exchangeSync = new ExchangeSync(exchangeIo, config.pair)

  val runner = new Runner(config, exchangeIo, exchangeSync)

  val docsRoute = new DocsRoute

  val routes = new Routes(exchangeIo)

  val host = "0.0.0.0"
  val port = 8080
  val bindingFuture = Http().newServerAt(host, port).bind(Route.seal(routes.route ~ docsRoute.route))

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
