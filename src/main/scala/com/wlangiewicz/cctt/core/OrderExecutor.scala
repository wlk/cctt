package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.CalculatedOrder
import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.service.trade.TradeService

object OrderExecutor extends LazyLogging {

  private def getOrder(trade: CalculatedOrder, pair: CurrencyPair) =
    // id = "id" and timestamp = null are ignored later
    new LimitOrder(OrderType.ASK, trade.amount.bigDecimal, pair, "id", null, trade.price.bigDecimal)

  def placeOrderIfValid(
      trade: CalculatedOrder,
      tradeService: TradeService,
      pair: CurrencyPair
    ): Unit =
    if (!trade.isValid) {
      logger.info(s"TradeEngine returned invalid trade $trade, not trading ...")
    } else {
      logger.info(s"TradeEngine returned trade $trade, executing")

      val order = getOrder(trade, pair)

      val placedOrderId = tradeService.placeLimitOrder(order)
      logger.info(s"Placed order id: $placedOrderId")
    }

  def shouldPlaceNewOrder(
      openOrders: Seq[LimitOrder],
      deletedOrderIds: Set[String],
      calculatedOrder: CalculatedOrder
    ): Boolean = {
    val notDeletedOrders = openOrders.filterNot(order => deletedOrderIds.contains(order.getId))

    notDeletedOrders match {
      // It's OK to ignore a scenario when there are more orders in that list, shouldn't happen in practice and should be resolved during next tick
      case h :: _ =>
        !(h.getLimitPrice == calculatedOrder.price.bigDecimal && h.getOriginalAmount == calculatedOrder.amount.bigDecimal)
      case _ => true
    }
  }
}
