package com.wlangiewicz.cctt.core

import java.util.Date

import com.wlangiewicz.cctt.data.CalculatedOrder
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.trade.LimitOrder
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class OrderCancellationServiceTest extends AnyWordSpec with should.Matchers {
  private val now = new Date(System.currentTimeMillis())

  "OrderCancellationService" when {
    "nonMatchingOrders" should {
      "return true if orders don't match" in {
        val trade = CalculatedOrder(1, 1)
        val order = new LimitOrder(
          OrderType.ASK,
          BigDecimal(1).bigDecimal,
          CurrencyPair.BTC_USD,
          "",
          now,
          BigDecimal(4300).bigDecimal
        )

        OrderCancellationService.nonMatchingOrders(trade.price)(order) shouldBe true
      }

      "return false if orders match" in {
        val trade = CalculatedOrder(1, 1)
        val order = new LimitOrder(
          OrderType.ASK,
          BigDecimal(1).bigDecimal,
          CurrencyPair.BTC_USD,
          "",
          now,
          BigDecimal(1).bigDecimal
        )

        OrderCancellationService.nonMatchingOrders(trade.price)(order) shouldBe false
      }
    }

    "orderIdsToCancel" should {
      "list all order Ids if trade is None" in {
        val trades = List[LimitOrder](
          new LimitOrder(
            OrderType.ASK,
            BigDecimal(1).bigDecimal,
            CurrencyPair.BTC_USD,
            "id",
            now,
            BigDecimal(4300).bigDecimal
          )
        )

        OrderCancellationService.orderIdsToCancel(None, trades) shouldBe List("id")
      }

      "list only all non matching orders (only price matters)" in {
        val trades = List[LimitOrder](
          new LimitOrder(
            OrderType.ASK,
            BigDecimal(1).bigDecimal,
            CurrencyPair.BTC_USD,
            "id1",
            now,
            BigDecimal(4300).bigDecimal
          ),
          new LimitOrder(
            OrderType.ASK,
            BigDecimal(1).bigDecimal,
            CurrencyPair.BTC_USD,
            "id2",
            now,
            BigDecimal(1).bigDecimal
          ),
          new LimitOrder(
            OrderType.ASK,
            BigDecimal(4300).bigDecimal,
            CurrencyPair.BTC_USD,
            "id3",
            now,
            BigDecimal(1).bigDecimal
          )
        )
        OrderCancellationService.orderIdsToCancel(Some(1), trades) shouldBe List("id1")
      }
    }
  }
}
