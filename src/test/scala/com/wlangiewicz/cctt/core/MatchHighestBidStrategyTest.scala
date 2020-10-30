package com.wlangiewicz.cctt.core

import org.knowm.xchange.currency.Currency
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class MatchHighestBidStrategyTest extends AnyWordSpec with should.Matchers {
  private val sellCurrency = Currency.BTC
  private val tradeAmount = BigDecimal("0.1")
  private val strategy = MatchHighestBidStrategy(sellCurrency)

  "MatchHighestBidStrategy" should {

    s"return $tradeAmount amount and price = lowestAsk when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderPriceCalculator.calculatePrice(AccountInfoTestHelper.hasBtcAndUsd, orderBook, strategy)
      result shouldBe Some(highestBid)
    }
  }
}
