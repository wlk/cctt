package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.config.ApplicationConfig
import com.wlangiewicz.cctt.data.{CalculatedOrder, ExchangeState}
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class OneBelowLowestAskStrategyTest extends AnyWordSpec with should.Matchers {
  val sellCurrency = ApplicationConfig.Config.sellCurrency
  val sellAmount = BigDecimal("0.1")
  val strategy = OneBelowLowestAskStrategy(sellAmount, sellCurrency)

  "OneBelowLowestAskStrategy" should {
    s"return $sellAmount amount and price = lowestAsk - 0.01 when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderCalculationService.calculateOrder(ExchangeState(orderBook, AccountInfoTestHelper.hasBtcAndUsd), strategy)
      result shouldBe Some(CalculatedOrder(sellAmount, lowestAsk - 0.01))
    }

    "return highest BID price if spread is less than 0.01" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4300 - 0.0001)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderCalculationService.calculateOrder(ExchangeState(orderBook, AccountInfoTestHelper.hasBtcAndUsd), strategy)
      result shouldBe Some(CalculatedOrder(sellAmount, highestBid))
    }
  }
}
