package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.ExchangeState
import org.knowm.xchange.currency.CurrencyPair

class ExchangeSync(exchange: ExchangeWrapper, pair: CurrencyPair) {

  def getExchangeState = {
    val orderBook = exchange.marketService.getOrderBook(pair)
    val accountInfo = exchange.accountService.getAccountInfo

    ExchangeState(orderBook, accountInfo)
  }
}
