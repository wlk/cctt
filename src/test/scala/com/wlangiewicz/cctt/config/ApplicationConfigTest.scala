package com.wlangiewicz.cctt.config

import com.wlangiewicz.cctt.config.ApplicationConfig.CCTTConfig
import com.wlangiewicz.cctt.core._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._

class ApplicationConfigTest extends AnyFlatSpec with should.Matchers {
  val sellCurrency = ApplicationConfig.Config.sellCurrency

  "CCTTConfig" should "return correct sell strategy" in {
    CCTTConfig.sellStrategy("NoOpSellStrategy", 1, sellCurrency) shouldBe NoOpSellStrategy
    CCTTConfig.sellStrategy("MatchLowestAskStrategy", 1, sellCurrency) shouldBe MatchLowestAskStrategy(
      1,
      sellCurrency
    )
    CCTTConfig.sellStrategy("OneBelowLowestAskStrategy", 1, sellCurrency) shouldBe OneBelowLowestAskStrategy(
      1,
      sellCurrency
    )
    CCTTConfig.sellStrategy("MatchHighestBidStrategy", 1, sellCurrency) shouldBe MatchHighestBidStrategy(
      1,
      sellCurrency
    )
    CCTTConfig.sellStrategy("OneAboveHighestBidStrategy", 1, sellCurrency) shouldBe OneAboveHighestBidStrategy(
      1,
      sellCurrency
    )
  }

  it should "throw if invalid sell strategy provided" in {
    an[InvalidSellStrategyException] should be thrownBy CCTTConfig.sellStrategy("abc", 1, sellCurrency)
  }

}
