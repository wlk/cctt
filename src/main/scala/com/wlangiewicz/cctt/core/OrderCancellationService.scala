package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.CalculatedOrder
import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.service.trade.TradeService

import scala.collection.immutable.Seq

object OrderCancellationService extends LazyLogging {

  private[core] def nonMatchingOrders(trade: CalculatedOrder)(order: LimitOrder) =
    order.getOriginalAmount != trade.amount.bigDecimal || order.getLimitPrice != trade.price.bigDecimal

  private[core] def orderIdsToCancel(maybeTrade: Option[CalculatedOrder], openOrders: Seq[LimitOrder]): Seq[String] = {
    val ordersToCancel = maybeTrade match {
      case Some(trade) =>
        openOrders.filter(nonMatchingOrders(trade))
      case None =>
        logger.info(s"Cancelling all open orders")
        openOrders
    }

    ordersToCancel.map(_.getId)
  }

  private[this] def performOrderCancellation(orderIds: Seq[String], tradeService: TradeService) =
    orderIds.map { id =>
      logger.info(s"Cancelling order: $id")
      tradeService.cancelOrder(id)
      id
    }

  def run(
      maybeTrade: Option[CalculatedOrder],
      openOrders: Seq[LimitOrder],
      tradeService: TradeService
    ): Seq[String] = {
    val ids = orderIdsToCancel(maybeTrade, openOrders)
    performOrderCancellation(ids, tradeService)
  }
}
