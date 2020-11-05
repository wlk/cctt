package com.wlangiewicz.cctt.http.data

import java.time.LocalDateTime

import com.wlangiewicz.cctt.core.{NoOpTradeStrategy, TradeStrategy}
import com.wlangiewicz.cctt.data.TradeStatus
import org.knowm.xchange.currency.CurrencyPair

case class Trade(
    id: TradeId,
    status: TradeStatus.Value,
    primaryAmount: BigDecimal,
    currencyPair: CurrencyPair,
    strategy: TradeStrategy,
    created: LocalDateTime)

object Trade {

  val example =
    Trade(TradeId.example, TradeStatus.New, 0.5, CurrencyPair.BTC_USD, NoOpTradeStrategy, LocalDateTime.now())
}
