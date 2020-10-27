package com.wlangiewicz.cctt

import com.wlangiewicz.cctt.config.ApplicationConfig.SellbotConfig
import com.wlangiewicz.cctt.core._
import com.typesafe.scalalogging.LazyLogging
import org.knowm.xchange.service.trade.TradeService

import scala.annotation.tailrec
import scala.util.Try

class Runner(
    config: SellbotConfig,
    tradeService: TradeService,
    exchangeSync: ExchangeSync)
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

  private def tick(): Unit = {
    val exchangeState = exchangeSync.getExchangeState

    logger.debug(s"AccountInfo: ${exchangeState.accountInfo.toString}")

    val openOrders = OpenOrdersService.currencyOpenOrders(config.pair, tradeService)
    logger.debug(s"Open Orders: ${openOrders.toString}")

    val calculatedOrder = OrderCalculationService.calculateOrder(exchangeState, config.sellStrategy)

    val deletedOrderIds = OrderCancellationService.run(calculatedOrder, openOrders, tradeService).toSet

    calculatedOrder match {
      case Some(trade) if OrderExecutor.shouldPlaceNewOrder(openOrders, deletedOrderIds, trade) =>
        logger.info(s"TradeEngine returned a trade $trade to create")
        OrderExecutor.placeOrderIfValid(trade, tradeService, config.pair, config.minSellPrice)
      case Some(trade) =>
        logger.info(
          s"TradeEngine returned a trade, but existing trade ${trade.amount}@${trade.price} is still the best one"
        )
      case None =>
        logger.info("TradeEngine did not return a trade, not trading")
    }

  }
}
