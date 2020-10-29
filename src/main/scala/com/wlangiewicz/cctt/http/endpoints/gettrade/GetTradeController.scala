package com.wlangiewicz.cctt.http.endpoints.gettrade

import com.wlangiewicz.cctt.http.data.{ApiError, AuthToken, Trade, TradeId}

import scala.concurrent.Future

class GetTradeController {

  def getTrade(token: AuthToken, tradeId: TradeId): Future[Either[ApiError, Trade]] =
    Future.successful(Right(Trade.example))

}
