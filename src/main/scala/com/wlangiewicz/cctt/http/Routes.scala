package com.wlangiewicz.cctt.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.wlangiewicz.cctt.core.BaseExchangeIo

class Routes(exchangeIo: BaseExchangeIo) {

  val route: Route =
    pathPrefix("asodfijsaofdjiowierj") {
      get {
        path("accountService") {
          complete(exchangeIo.getAccountInfo.toString)
        } ~
          path("tradeService") {
            complete(exchangeIo.getOpenOrders.toString)
          }
      }
    }
}
