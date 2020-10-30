package com.wlangiewicz.cctt.core

import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.data.ExchangeState

object OrderPriceCalculator extends LazyLogging {

  def calculatePrice(exchangeInfo: ExchangeState, tradeStrategy: TradeStrategy): Option[BigDecimal] =
    tradeStrategy.getPrice(exchangeInfo)
}
