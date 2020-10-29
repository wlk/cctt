package com.wlangiewicz.cctt.http.endpoints.gettrade

import akka.http.scaladsl.server.Route
import com.wlangiewicz.cctt.http.ApiRoute
import sttp.tapir.server.akkahttp._

class GetTradeRoute(controller: GetTradeController) extends ApiRoute {
  override def route: Route = GetTradeEndpoint.getTradeEndpoint.toRoute((controller.getTrade _).tupled)
}
