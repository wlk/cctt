package com.wlangiewicz.cctt.http.endpoints.newtrade

import com.wlangiewicz.cctt.http.data.{ApiError, AuthToken, TradeId}

import scala.concurrent.Future

class NewTradeController {

  def newTrade(token: AuthToken, newTradeRequest: NewTradeRequest): Future[Either[ApiError, TradeId]] =
    Future.successful(Right(TradeId.example))

}
