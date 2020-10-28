package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.{CalculatedOrder, OrderId}
import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.dto.trade.LimitOrder

import scala.collection.immutable.Seq

object OrderCancellationService extends LazyLogging {

  private[core] def nonMatchingOrders(trade: CalculatedOrder)(order: LimitOrder): Boolean =
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

  private[this] def performOrderCancellation(orderIds: Seq[String], exchangeIo: BaseExchangeIo): Seq[String] =
    orderIds.map { id =>
      logger.info(s"Cancelling order: $id")
      exchangeIo.cancelLimitOrder(OrderId(id))
      id
    }

  def run(
      maybeTrade: Option[CalculatedOrder],
      openOrders: Seq[LimitOrder],
      exchangeIo: BaseExchangeIo
    ): Seq[String] = {
    val ids = orderIdsToCancel(maybeTrade, openOrders)
    performOrderCancellation(ids, exchangeIo)
  }
}
