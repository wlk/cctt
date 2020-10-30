package com.wlangiewicz.cctt.data

import com.wlangiewicz.cctt.http.EnumHelper

case class CalculatedOrder(amount: BigDecimal, price: BigDecimal) {
  def isValid: Boolean = amount > 0 && price > 0
}

case class OrderId(value: String)

object TradeStatus extends Enumeration with EnumHelper {
  type TradeStatus = Value
  val New, InProgress, Done, Cancelled = Value
}
