package com.wlangiewicz.cctt

import com.wlangiewicz.cctt.core._
import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.config.ExchangeName
import com.wlangiewicz.cctt.dao.TradeDao
import com.wlangiewicz.cctt.http.data.Trade
import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.marketdata.OrderBook

import scala.annotation.tailrec
import scala.util.Try

class Runner(
    exchangeIo: BaseExchangeIo,
    tradeDao: TradeDao,
    exchange: ExchangeName.Value)
    extends LazyLogging {

  @tailrec
  final def loop(sleep: Long): Unit = {

    Try(tick()).recover {
      case ex =>
        logger.warn("Exception during tick", ex)
    }

    Thread.sleep(sleep)
    loop(sleep)
  }

  def getOrderToProcess: Option[Trade] =
    None

  def getDecisionForTrade(trade: Trade): TradeDecision =
    Hold

  def getRemainingTradeAmount(trade: Trade): BigDecimal =
    0

  def cancelTradeOrders(trade: Trade) =
    ()

  def createOrder(
      accountInfo: AccountInfo,
      orderBook: OrderBook,
      trade: Trade
    ): Unit = {
    val calculatedOrder = OrderPriceCalculator.calculatePrice(accountInfo, orderBook, trade.strategy)
    calculatedOrder match {
      case Some(price) =>
        logger.info(s"TradeEngine returned a trade $price to create")
        OrderExecutor.placeOrderIfValid(price, getRemainingTradeAmount(trade), exchangeIo, trade.currencyPair)
      case None =>
        logger.info("TradeEngine did not return a trade, not trading")
    }
  }

  private def tick(): Unit =
    getOrderToProcess match {
      case None => logger.info("No trade to process")
      case Some(trade) =>
        val accountInfo = exchangeIo.getAccountInfo
        val orderBook = exchangeIo.getOrderBook(trade.currencyPair)

        logger.debug(s"AccountInfo: ${accountInfo.toString}")
        val openOrders = exchangeIo.getOpenOrders(trade.currencyPair)
        logger.debug(s"Open Orders: ${openOrders.toString}")

        getDecisionForTrade(trade) match {
          case Hold   => logger.info(s"Holding")
          case Cancel => cancelTradeOrders(trade)
          case Create => createOrder(accountInfo, orderBook, trade)
        }
    }
}
