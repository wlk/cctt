package com.wlangiewicz.cctt.config

import com.wlangiewicz.cctt.config.ApplicationConfig.SellbotConfig
import com.wlangiewicz.cctt.core._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._

class ApplicationConfigTest extends AnyFlatSpec with should.Matchers {
  val sellCurrency = ApplicationConfig.Config.sellCurrency

  "SellbotConfig" should "return correct sell strategy" in {
    SellbotConfig.sellStrategy("NoOpSellStrategy", 1, sellCurrency) shouldBe NoOpSellStrategy
    SellbotConfig.sellStrategy("MatchLowestAskStrategy", 1, sellCurrency) shouldBe MatchLowestAskStrategy(
      1,
      sellCurrency
    )
    SellbotConfig.sellStrategy("OneBelowLowestAskStrategy", 1, sellCurrency) shouldBe OneBelowLowestAskStrategy(
      1,
      sellCurrency
    )
    SellbotConfig.sellStrategy("MatchHighestBidStrategy", 1, sellCurrency) shouldBe MatchHighestBidStrategy(
      1,
      sellCurrency
    )
    SellbotConfig.sellStrategy("OneAboveHighestBidStrategy", 1, sellCurrency) shouldBe OneAboveHighestBidStrategy(
      1,
      sellCurrency
    )
  }

  it should "throw if invalid sell strategy provided" in {
    an[InvalidSellStrategyException] should be thrownBy SellbotConfig.sellStrategy("abc", 1, sellCurrency)
  }

}
