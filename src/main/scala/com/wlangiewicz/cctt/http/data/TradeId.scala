package com.wlangiewicz.cctt.http.data

import java.util.UUID

case class TradeId(value: UUID)

object TradeId {
  val example = TradeId(UUID.randomUUID())
}
