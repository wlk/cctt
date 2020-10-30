package com.wlangiewicz.cctt

import com.wlangiewicz.cctt.core._
import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.currency.CurrencyPair

import scala.annotation.tailrec
import scala.util.Try

class Runner(exchangeIo: BaseExchangeIo, exchangeSync: ExchangeSync) extends LazyLogging {

  @tailrec
  final def loop(sleep: Long): Unit = {

    Try(tick()).recover {
      case ex =>
        logger.warn("Exception during tick", ex)
    }

    Thread.sleep(sleep)
    loop(sleep)
  }

  private def tick(): Unit = {
    val pair = CurrencyPair.BTC_EUR
    val tradeStrategy = NoOpTradeStrategy
    val exchangeState = exchangeSync.getExchangeState(pair)

    logger.debug(s"AccountInfo: ${exchangeState.accountInfo.toString}")

    val openOrders = exchangeIo.getOpenOrders(pair)
    logger.debug(s"Open Orders: ${openOrders.toString}")

    val calculatedOrder = OrderPriceCalculator.calculatePrice(exchangeState, tradeStrategy)

    val deletedOrderIds = OrderCancellationService.run(calculatedOrder, openOrders, exchangeIo).toSet

    calculatedOrder match {
      case Some(price) if OrderExecutor.shouldPlaceNewOrder(openOrders, deletedOrderIds, price) =>
        logger.info(s"TradeEngine returned a trade $price to create")
        val amount: BigDecimal = 1
        OrderExecutor.placeOrderIfValid(price, amount, exchangeIo, pair)
      case Some(price) =>
        logger.info(
          s"TradeEngine returned a trade, but existing trade at price $price is still the best one"
        )
      case None =>
        logger.info("TradeEngine did not return a trade, not trading")
    }

  }
}
