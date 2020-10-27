package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.{CalculatedOrder, ExchangeState}
import com.typesafe.scalalogging.LazyLogging

object OrderCalculationService extends LazyLogging {

  def calculateOrder(exchangeInfo: ExchangeState, sellStrategy: SellStrategy): Option[CalculatedOrder] =
    sellStrategy.getOrder(exchangeInfo)
}
