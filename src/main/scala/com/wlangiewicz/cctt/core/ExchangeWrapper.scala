package com.wlangiewicz.cctt.core

import org.knowm.xchange.{BaseExchange, ExchangeFactory}

class ExchangeWrapper(
    exchange: BaseExchange,
    key: String,
    secret: String) {

  private val exchangeInstance = {
    val exchangeSpecification = exchange.getDefaultExchangeSpecification
    exchangeSpecification.setApiKey(key)
    exchangeSpecification.setSecretKey(secret)
    ExchangeFactory.INSTANCE.createExchange(exchangeSpecification)
  }

  val marketService = exchangeInstance.getMarketDataService
  val accountService = exchangeInstance.getAccountService
  val tradeService = exchangeInstance.getTradeService
}
