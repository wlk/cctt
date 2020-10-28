package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.ExchangeState
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class NoOpTradeStrategyTest extends AnyWordSpec with should.Matchers {
  "NoOpTradeStrategy" should {
    "return 0 amount when empty order book provided" in {
      val result = OrderCalculationService.calculateOrder(
        ExchangeState(OrderBookTestHelper.empty, AccountInfoTestHelper.empty),
        NoOpTradeStrategy
      )
      result shouldBe None
    }

    "return 0 amount when non-empty order book provided" in {
      val result = OrderCalculationService.calculateOrder(
        ExchangeState(OrderBookTestHelper.singleValue, AccountInfoTestHelper.empty),
        NoOpTradeStrategy
      )
      result shouldBe None
    }

    "return 0 amount when non-empty order book provided and account has funds" in {
      val result = OrderCalculationService.calculateOrder(
        ExchangeState(OrderBookTestHelper.singleValue, AccountInfoTestHelper.hasBtcAndUsd),
        NoOpTradeStrategy
      )
      result shouldBe None
    }
  }
}
