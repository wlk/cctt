package com.wlangiewicz.cctt.data

import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.marketdata.OrderBook

case class ExchangeState(orderBook: OrderBook, accountInfo: AccountInfo)

case class CalculatedOrder(amount: BigDecimal, price: BigDecimal) {
  def isValid = amount > 0 && price > 0
}
