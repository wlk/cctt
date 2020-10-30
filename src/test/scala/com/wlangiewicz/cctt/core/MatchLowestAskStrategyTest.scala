package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.ExchangeState
import org.knowm.xchange.currency.Currency
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class MatchLowestAskStrategyTest extends AnyWordSpec with should.Matchers {
  private val sellCurrency = Currency.BTC
  private val tradeAmount = BigDecimal("0.1")
  private val strategy = MatchLowestAskStrategy(sellCurrency)

  "MatchLowestAskStrategy" should {
    "return 0 amount when empty order book provided" in {
      val result = OrderPriceCalculator.calculatePrice(
        ExchangeState(OrderBookTestHelper.empty, AccountInfoTestHelper.empty),
        strategy
      )
      result shouldBe None
    }

    "return 0 amount when non-empty order book provided but account balance is 0" in {
      val result = OrderPriceCalculator.calculatePrice(
        ExchangeState(OrderBookTestHelper.singleValue, AccountInfoTestHelper.empty),
        strategy
      )
      result shouldBe None
    }

    s"return $tradeAmount amount and price = lowestAsk when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderPriceCalculator.calculatePrice(ExchangeState(orderBook, AccountInfoTestHelper.hasBtcAndUsd), strategy)
      result shouldBe Some(lowestAsk)
    }
  }
}
