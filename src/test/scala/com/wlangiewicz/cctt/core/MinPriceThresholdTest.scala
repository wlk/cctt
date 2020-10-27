package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.CalculatedOrder
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec

class MinPriceThresholdTest extends AnyWordSpec with should.Matchers {
  "MinPriceThreshold" when {
    "isBelowThreshold" should {
      "return true if below threshold" in {
        MinPriceThreshold.isBelowThreshold(CalculatedOrder(10, 1000), 3500) shouldBe true
      }

      "return false if above or equal to threshold" in {
        MinPriceThreshold.isBelowThreshold(CalculatedOrder(10, 1000), 500) shouldBe false
        MinPriceThreshold.isBelowThreshold(CalculatedOrder(10, 500), 500) shouldBe false
      }
    }
  }
}
