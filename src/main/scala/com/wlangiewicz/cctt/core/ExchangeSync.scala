package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.ExchangeState
import org.knowm.xchange.currency.CurrencyPair

class ExchangeSync(exchangeIo: BaseExchangeIo, pair: CurrencyPair) {

  def getExchangeState: ExchangeState = {
    val orderBook = exchangeIo.getOrderBook(pair)
    val accountInfo = exchangeIo.getAccountInfo

    ExchangeState(orderBook, accountInfo)
  }
}
