package com.wlangiewicz.cctt.http.endpoints.newtrade

import com.wlangiewicz.cctt.config.ExchangeName
import com.wlangiewicz.cctt.http.data.OrderType

case class NewTradeRequest(
    primaryAmount: BigDecimal,
    orderType: OrderType.Value,
    exchange: ExchangeName.Value)

object NewTradeRequest {
  val example = NewTradeRequest(1.23, OrderType.Bid, ExchangeName.Kraken)
}
