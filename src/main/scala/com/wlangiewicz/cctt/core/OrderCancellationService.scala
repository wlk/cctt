package com.wlangiewicz.cctt.core

import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.data.OrderId
import org.knowm.xchange.dto.trade.LimitOrder

import scala.collection.immutable.Seq

object OrderCancellationService extends LazyLogging {

  private[core] def nonMatchingOrders(price: BigDecimal)(order: LimitOrder): Boolean =
    order.getLimitPrice != price.bigDecimal

  private[core] def orderIdsToCancel(maybePrice: Option[BigDecimal], openOrders: Seq[LimitOrder]): Seq[String] = {
    val ordersToCancel = maybePrice match {
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
      maybeTrade: Option[BigDecimal],
      openOrders: Seq[LimitOrder],
      exchangeIo: BaseExchangeIo
    ): Seq[String] = {
    val ids = orderIdsToCancel(maybeTrade, openOrders)
    performOrderCancellation(ids, exchangeIo)
  }
}
