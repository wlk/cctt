package com.wlangiewicz.cctt.http.endpoints.newtrade

import akka.http.scaladsl.server.Route
import com.wlangiewicz.cctt.http.ApiRoute
import sttp.tapir.server.akkahttp._

class NewTradeRoute(controller: NewTradeController) extends ApiRoute {
  override def route: Route = NewTradeEndpoint.newTradeEndpoint.toRoute((controller.newTrade _).tupled)
}
