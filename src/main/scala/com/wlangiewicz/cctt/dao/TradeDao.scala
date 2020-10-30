package com.wlangiewicz.cctt.dao

import com.wlangiewicz.cctt.data.TradeStatus
import com.wlangiewicz.cctt.http.data.{Trade, TradeId}

trait TradeDao {
  def store(trade: Trade): Unit
  def update(trade: Trade): Unit
  def getById(tradeId: TradeId): Option[Trade]
  def getByStatus(tradeStatus: TradeStatus.Value): List[Trade]
}

class InMemoryTradeDao extends TradeDao {
  private val store = scala.collection.mutable.HashMap.empty[TradeId, Trade]

  override def store(trade: Trade): Unit =
    store.addOne((trade.id, trade))

  override def update(trade: Trade): Unit =
    store.update(trade.id, trade)

  override def getById(tradeId: TradeId): Option[Trade] =
    store.get(tradeId)

  override def getByStatus(tradeStatus: TradeStatus.Value): List[Trade] =
    store
      .filter {
        case (_, trade) => trade.status == tradeStatus
      }
      .values
      .toList
      .sortBy(_.created)
}
