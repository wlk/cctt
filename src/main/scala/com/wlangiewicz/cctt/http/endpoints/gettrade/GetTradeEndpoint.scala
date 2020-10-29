package com.wlangiewicz.cctt.http.endpoints.gettrade

import com.wlangiewicz.cctt.http.ApiBaseEndpoint
import com.wlangiewicz.cctt.http.data._
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

object GetTradeEndpoint extends ApiBaseEndpoint {

  val getTradeEndpoint: Endpoint[
    (AuthToken, TradeId),
    ApiError,
    Trade,
    Any
  ] =
    baseEndpoint.post
      .name("Get Trade")
      .tag("Trades")
      .in("trade" / path[TradeId]("tradeId"))
      .out(jsonBody[Trade].example(Trade.example))

}
