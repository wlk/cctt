package com.wlangiewicz.cctt.core

import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.trade.LimitOrder

object OrderExecutor extends LazyLogging {

  private def getOrder(
      price: BigDecimal,
      amount: BigDecimal,
      pair: CurrencyPair
    ) =
    // id = "id" and timestamp = null are ignored later
    new LimitOrder(OrderType.ASK, amount.bigDecimal, pair, "id", null, price.bigDecimal)

  def placeOrderIfValid(
      price: BigDecimal,
      amount: BigDecimal,
      exchangeIo: BaseExchangeIo,
      pair: CurrencyPair
    ): Unit =
    if (price <= 0) {
      logger.info(s"TradeEngine returned invalid trade $price, not trading ...")
    } else {
      logger.info(s"TradeEngine returned trade $price, executing")

      val order = getOrder(price, amount, pair)

      val placedOrderId = exchangeIo.placeLimitOrder(order)
      logger.info(s"Placed order id: $placedOrderId")
    }

  def shouldPlaceNewOrder(
      openOrders: Seq[LimitOrder],
      deletedOrderIds: Set[String],
      price: BigDecimal
    ): Boolean = {
    val notDeletedOrders = openOrders.filterNot(order => deletedOrderIds.contains(order.getId))

    notDeletedOrders match {
      // It's OK to ignore a scenario when there are more orders in that list, shouldn't happen in practice and should be resolved during next tick
      case h :: _ =>
        !(h.getLimitPrice == price.bigDecimal)
      case _ => true
    }
  }
}
