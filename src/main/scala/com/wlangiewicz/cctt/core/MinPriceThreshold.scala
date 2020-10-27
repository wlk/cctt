package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.CalculatedOrder
import com.typesafe.scalalogging.LazyLogging

object MinPriceThreshold extends LazyLogging {

  def isBelowThreshold(order: CalculatedOrder, minPrice: BigDecimal) =
    order.price < minPrice
}
