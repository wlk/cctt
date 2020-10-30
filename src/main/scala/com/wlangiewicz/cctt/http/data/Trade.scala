package com.wlangiewicz.cctt.http.data

import java.time.LocalDateTime

import com.wlangiewicz.cctt.data.TradeStatus

case class Trade(
    id: TradeId,
    status: TradeStatus.Value,
    created: LocalDateTime)

object Trade {
  val example = Trade(TradeId.example, TradeStatus.New, LocalDateTime.now())
}
