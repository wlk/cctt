package com.wlangiewicz.cctt.core

import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.marketdata.OrderBook

object OrderPriceCalculator extends LazyLogging {

  def calculatePrice(
      accountInfo: AccountInfo,
      orderBook: OrderBook,
      tradeStrategy: TradeStrategy
    ): Option[BigDecimal] =
    tradeStrategy.getPrice(accountInfo, orderBook)
}
