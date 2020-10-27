package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.CalculatedOrder
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.trade.LimitOrder
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class OrderExecutorTest extends AnyWordSpec with should.Matchers {
  "OrderExecutor" when {
    "shouldPlaceNewOrder" should {
      "return true if we don't have any open orders" in {
        val calculatedOrder = CalculatedOrder(10, 5900)
        OrderExecutor.shouldPlaceNewOrder(Seq.empty, Set.empty, calculatedOrder) shouldBe true
      }

      "return true if calculated order price is not equal to top of the order book" in {
        // This shouldn't happen in practice
        val orderAmount = BigDecimal(10)
        val orderPrice = BigDecimal(5900)
        val calculatedOrder = CalculatedOrder(orderAmount, orderPrice)
        val ourOpenOrders = List(
          new LimitOrder(
            OrderType.ASK,
            (calculatedOrder.amount - 1).bigDecimal,
            CurrencyPair.BTC_USD,
            "id",
            null,
            (calculatedOrder.price - 1).bigDecimal
          )
        )

        OrderExecutor.shouldPlaceNewOrder(ourOpenOrders, Set.empty, calculatedOrder) shouldBe true
      }

      "return false if calculated order price and amount are equal to the top of the order book" in {
        val orderAmount = BigDecimal(10)
        val orderPrice = BigDecimal(5900)
        val calculatedOrder = CalculatedOrder(orderAmount, orderPrice)
        val ourOpenOrders = List(
          new LimitOrder(
            OrderType.ASK,
            calculatedOrder.amount.bigDecimal,
            CurrencyPair.BTC_USD,
            "id",
            null,
            calculatedOrder.price.bigDecimal
          )
        )

        OrderExecutor.shouldPlaceNewOrder(ourOpenOrders, Set.empty, calculatedOrder) shouldBe false
      }
    }
  }
}
