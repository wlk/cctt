package com.wlangiewicz.cctt.core

import org.knowm.xchange.currency.Currency
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class OneAboveHighestBidStrategyTest extends AnyWordSpec with should.Matchers {
  private val sellCurrency = Currency.BTC
  private val tradeAmount = BigDecimal("0.1")
  private val strategy = OneAboveHighestBidStrategy(sellCurrency)

  "OneBelowLowestAskStrategy" should {
    s"return $tradeAmount amount and price = lowestAsk + 0.01 when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderPriceCalculator.calculatePrice(AccountInfoTestHelper.hasBtcAndUsd, orderBook, strategy)
      result shouldBe Some(highestBid + 0.01)
    }

    "return lowest ASK price if spread is less than 0.01" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4300 - 0.0001)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderPriceCalculator.calculatePrice(AccountInfoTestHelper.hasBtcAndUsd, orderBook, strategy)
      result shouldBe Some(lowestAsk)
    }
  }
}
