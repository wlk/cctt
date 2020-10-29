package com.wlangiewicz.cctt.http.endpoints.newtrade

import com.wlangiewicz.cctt.http.ApiBaseEndpoint
import com.wlangiewicz.cctt.http.data._
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

object NewTradeEndpoint extends ApiBaseEndpoint {

  val newTradeEndpoint: Endpoint[
    (AuthToken, NewTradeRequest),
    ApiError,
    TradeId,
    Any
  ] =
    baseEndpoint.post
      .name("Create new Trade")
      .tag("Trades")
      .in("trades" / "new")
      .in(jsonBody[NewTradeRequest].example(NewTradeRequest.example))
      .out(jsonBody[TradeId].example(TradeId.example))

}
