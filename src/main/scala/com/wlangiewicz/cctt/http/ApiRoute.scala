package com.wlangiewicz.cctt.http

import akka.http.scaladsl.server.Route

trait ApiRoute {
  def route: Route
}
