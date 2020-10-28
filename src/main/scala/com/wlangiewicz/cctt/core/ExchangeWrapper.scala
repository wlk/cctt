package com.wlangiewicz.cctt.core

import org.knowm.xchange.service.account.AccountService
import org.knowm.xchange.service.marketdata.MarketDataService
import org.knowm.xchange.service.trade.TradeService
import org.knowm.xchange.{BaseExchange, ExchangeFactory}

trait BaseExchangeWrapper {
  val marketService: MarketDataService
  val accountService: AccountService
  val tradeService: TradeService
}

class ExchangeWrapper(
    exchange: BaseExchange,
    key: String,
    secret: String)
    extends BaseExchangeWrapper {

  private val exchangeInstance = {
    val exchangeSpecification = exchange.getDefaultExchangeSpecification
    exchangeSpecification.setApiKey(key)
    exchangeSpecification.setSecretKey(secret)
    ExchangeFactory.INSTANCE.createExchange(exchangeSpecification)
  }

  val marketService: MarketDataService = exchangeInstance.getMarketDataService
  val accountService: AccountService = exchangeInstance.getAccountService
  val tradeService: TradeService = exchangeInstance.getTradeService
}
