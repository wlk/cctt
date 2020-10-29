package com.wlangiewicz.cctt.http.endpoints.openorders

import akka.http.scaladsl.server.Route
import com.wlangiewicz.cctt.http.ApiRoute
import sttp.tapir.server.akkahttp._

class OpenOrdersRoute(controller: OpenOrdersController) extends ApiRoute {
  override def route: Route = OpenOrdersEndpoint.openOrdersEndpoint.toRoute(controller.getOpenOrders)
}
