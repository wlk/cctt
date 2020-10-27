package com.wlangiewicz.cctt.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.knowm.xchange.service.account.AccountService
import org.knowm.xchange.service.trade.TradeService

class Routes(accountService: AccountService, tradeService: TradeService) {

  val route: Route =
    pathPrefix("asodfijsaofdjiowierj") {
      get {
        path("accountService") {
          complete(accountService.getAccountInfo.toString)
        } ~
          path("tradeService") {
            complete(tradeService.getOpenOrders.toString)
          }
      }
    }
}
