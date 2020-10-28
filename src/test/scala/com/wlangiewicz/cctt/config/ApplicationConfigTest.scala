package com.wlangiewicz.cctt.config

import com.wlangiewicz.cctt.config.ApplicationConfig.CCTTConfig
import com.wlangiewicz.cctt.core._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._

class ApplicationConfigTest extends AnyFlatSpec with should.Matchers {
  private val sellCurrency = ApplicationConfig.Config.sellCurrency

  "CCTTConfig" should "return correct trade strategy" in {
    CCTTConfig.tradeStrategy("NoOpTradeStrategy", 1, sellCurrency) shouldBe NoOpTradeStrategy
    CCTTConfig.tradeStrategy("MatchLowestAskStrategy", 1, sellCurrency) shouldBe MatchLowestAskStrategy(
      1,
      sellCurrency
    )
    CCTTConfig.tradeStrategy("OneBelowLowestAskStrategy", 1, sellCurrency) shouldBe OneBelowLowestAskStrategy(
      1,
      sellCurrency
    )
    CCTTConfig.tradeStrategy("MatchHighestBidStrategy", 1, sellCurrency) shouldBe MatchHighestBidStrategy(
      1,
      sellCurrency
    )
    CCTTConfig.tradeStrategy("OneAboveHighestBidStrategy", 1, sellCurrency) shouldBe OneAboveHighestBidStrategy(
      1,
      sellCurrency
    )
  }

  it should "throw if invalid trade strategy provided" in {
    an[InvalidTradeStrategyException] should be thrownBy CCTTConfig.tradeStrategy("abc", 1, sellCurrency)
  }

}
