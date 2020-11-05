package com.wlangiewicz.cctt

import com.wlangiewicz.cctt.core._
import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.dao.TradeDao
import com.wlangiewicz.cctt.http.data.Trade

import scala.annotation.tailrec
import scala.util.Try

class Runner(exchangeIo: BaseExchangeIo, tradeDao: TradeDao) extends LazyLogging {

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
          case Hold   => ()
          case Cancel => ()
          case Create =>
            val calculatedOrder = OrderPriceCalculator.calculatePrice(accountInfo, orderBook, trade.strategy)
            calculatedOrder match {
              case Some(price) =>
                logger.info(s"TradeEngine returned a trade $price to create")
                val remainingAmount = trade.primaryAmount // todo
                OrderExecutor.placeOrderIfValid(price, remainingAmount, exchangeIo, trade.currencyPair)
              case None =>
                logger.info("TradeEngine did not return a trade, not trading")
            }

          case Move => ()
        }
    }
}
