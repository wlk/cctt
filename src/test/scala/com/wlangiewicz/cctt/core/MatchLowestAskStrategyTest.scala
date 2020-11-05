package com.wlangiewicz.cctt.core

import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class MatchLowestAskStrategyTest extends AnyWordSpec with should.Matchers {
  private val tradeAmount = BigDecimal("0.1")
  private val strategy = MatchLowestAskStrategy()

  "MatchLowestAskStrategy" should {
    "return None when empty order book provided" in {
      val result = OrderPriceCalculator.calculatePrice(
        AccountInfoTestHelper.empty,
        OrderBookTestHelper.empty,
        strategy
      )
      result shouldBe None
    }

    "return correct price when order book with only one order is provided" in {
      val result = OrderPriceCalculator.calculatePrice(
        AccountInfoTestHelper.empty,
        OrderBookTestHelper.singleValue,
        strategy
      )
      result shouldBe Some(4300)
    }

    s"return $tradeAmount  price = lowestAsk when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderPriceCalculator.calculatePrice(AccountInfoTestHelper.hasBtcAndUsd, orderBook, strategy)
      result shouldBe Some(lowestAsk)
    }
  }
}
