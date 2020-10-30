package com.wlangiewicz.cctt.core

import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class NoOpTradeStrategyTest extends AnyWordSpec with should.Matchers {
  "NoOpTradeStrategy" should {
    "return 0 amount when empty order book provided" in {
      val result = OrderPriceCalculator.calculatePrice(
        AccountInfoTestHelper.empty,
        OrderBookTestHelper.empty,
        NoOpTradeStrategy
      )
      result shouldBe None
    }

    "return 0 amount when non-empty order book provided" in {
      val result = OrderPriceCalculator.calculatePrice(
        AccountInfoTestHelper.empty,
        OrderBookTestHelper.singleValue,
        NoOpTradeStrategy
      )
      result shouldBe None
    }

    "return 0 amount when non-empty order book provided and account has funds" in {
      val result = OrderPriceCalculator.calculatePrice(
        AccountInfoTestHelper.hasBtcAndUsd,
        OrderBookTestHelper.singleValue,
        NoOpTradeStrategy
      )
      result shouldBe None
    }
  }
}
