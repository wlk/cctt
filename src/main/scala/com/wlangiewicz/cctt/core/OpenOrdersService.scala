package com.wlangiewicz.cctt.core

import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.service.trade.TradeService

import scala.jdk.CollectionConverters._
import scala.collection.immutable.Seq

object OpenOrdersService extends LazyLogging {

  def currencyOpenOrders(pair: CurrencyPair, tradeService: TradeService): Seq[LimitOrder] =
    tradeService
      .getOpenOrders(tradeService.createOpenOrdersParams)
      .getOpenOrders
      .asScala
      .filter(_.getInstrument == pair)
      .toList
}
