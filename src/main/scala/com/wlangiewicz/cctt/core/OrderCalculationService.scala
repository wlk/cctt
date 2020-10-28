package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.{CalculatedOrder, ExchangeState}
import com.typesafe.scalalogging.LazyLogging

object OrderCalculationService extends LazyLogging {

  def calculateOrder(exchangeInfo: ExchangeState, tradeStrategy: TradeStrategy): Option[CalculatedOrder] =
    tradeStrategy.getOrder(exchangeInfo)
}
