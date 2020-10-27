package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.config.ApplicationConfig
import com.wlangiewicz.cctt.data.{CalculatedOrder, ExchangeState}
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class MatchHighestBidStrategyTest extends AnyWordSpec with should.Matchers {
  val sellCurrency = ApplicationConfig.Config.sellCurrency
  val sellAmount = BigDecimal("0.1")
  val strategy = MatchHighestBidStrategy(sellAmount, sellCurrency)

  "MatchHighestBidStrategy" should {

    s"return $sellAmount amount and price = lowestAsk when non-empty order book provided and account has balance" in {
      val lowestAsk = BigDecimal(4300)
      val highestBid = BigDecimal(4200)
      val orderBook = OrderBookTestHelper.withTopOrders(lowestAsk, highestBid)

      val result =
        OrderCalculationService.calculateOrder(ExchangeState(orderBook, AccountInfoTestHelper.hasBtcAndUsd), strategy)
      result shouldBe Some(CalculatedOrder(sellAmount, highestBid))
    }
  }
}
