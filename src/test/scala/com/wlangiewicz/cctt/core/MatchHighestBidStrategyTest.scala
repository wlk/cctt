package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.config.ApplicationConfig
import com.wlangiewicz.cctt.data.{CalculatedOrder, ExchangeState}
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class MatchHighestBidStrategyTest extends AnyWordSpec with should.Matchers {
  private val sellCurrency = ApplicationConfig.Config.sellCurrency
  private val tradeAmount = BigDecimal("0.1")
  private val strategy = MatchHighestBidStrategy(tradeAmount, sellCurrency)

  "MatchHighestBidStrategy" should {

    s"return $tradeAmount amount and price = lowestAsk when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderCalculationService.calculateOrder(ExchangeState(orderBook, AccountInfoTestHelper.hasBtcAndUsd), strategy)
      result shouldBe Some(CalculatedOrder(tradeAmount, highestBid))
    }
  }
}
