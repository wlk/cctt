package com.wlangiewicz.cctt.http.endpoints.openorders

import com.wlangiewicz.cctt.http.data.{ApiError, AuthToken}

import scala.concurrent.Future

class OpenOrdersController {

  def getOpenOrders(token: AuthToken): Future[Either[ApiError, OpenOrdersResponse]] =
    Future.successful(Right(OpenOrdersResponse.example))

}
