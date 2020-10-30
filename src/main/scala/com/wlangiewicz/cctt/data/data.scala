package com.wlangiewicz.cctt.data

import com.wlangiewicz.cctt.http.EnumHelper
import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.marketdata.OrderBook

case class ExchangeState(orderBook: OrderBook, accountInfo: AccountInfo)

case class CalculatedOrder(amount: BigDecimal, price: BigDecimal) {
  def isValid: Boolean = amount > 0 && price > 0
}

case class OrderId(value: String)

object TradeStatus extends Enumeration with EnumHelper {
  type TradeStatus = Value
  val New, InProgress, Done, Cancelled = Value
}
