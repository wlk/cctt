package com.wlangiewicz.cctt.core

import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.marketdata.OrderBook

import scala.jdk.CollectionConverters._

sealed trait TradeStrategy {
  def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal]
}

sealed trait WithOrderType {

  def orderType: OrderType

  def opposingOrderType(orderType: OrderType): OrderType =
    if (orderType == OrderType.ASK) {
      OrderType.BID
    } else if (orderType == OrderType.BID) {
      OrderType.ASK
    } else {
      throw new RuntimeException(s"invalid order type: $orderType, only ASK and BID are supported")
    }

  def topOpposingOrderPrice(orderBook: OrderBook): java.math.BigDecimal =
    orderBook
      .getOrders(opposingOrderType(orderType))
      .asScala
      .sorted
      .headOption
      .map(_.getLimitPrice)
      .getOrElse(new java.math.BigDecimal(0))
}

case object NoOpTradeStrategy extends TradeStrategy {
  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] = None
}

private case class ExactMatchingStrategy(sellCurrency: Currency, override val orderType: OrderType)
    extends TradeStrategy
    with WithOrderType {

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] = {
    val walletAmount = BigDecimal(accountInfo.getWallet.getBalance(sellCurrency).getAvailable)

    orderBook.getOrders(orderType).asScala.sorted.headOption.map(_.getLimitPrice).flatMap { bestPrice =>
      if (walletAmount == 0 || bestPrice == BigDecimal(0).bigDecimal) {
        None
      } else {
        Some(bestPrice)
      }
    }
  }
}

case class MatchLowestAskStrategy(sellCurrency: Currency) extends TradeStrategy with WithOrderType {
  override val orderType = OrderType.ASK
  private val delegate = ExactMatchingStrategy(sellCurrency, orderType)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] =
    delegate.getPrice(accountInfo, orderBook)
}

case class OneBelowLowestAskStrategy(sellCurrency: Currency) extends TradeStrategy {
  private val delegate = MatchLowestAskStrategy(sellCurrency)
  private val delta = BigDecimal(0.01)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] = {
    val topOpposingOrder = delegate.topOpposingOrderPrice(orderBook)
    delegate.getPrice(accountInfo, orderBook).map(order => topOpposingOrder.max(order - delta))
  }
}

case class MatchHighestBidStrategy(sellCurrency: Currency) extends TradeStrategy with WithOrderType {
  override val orderType = OrderType.BID

  private val delegate = ExactMatchingStrategy(sellCurrency, orderType)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] =
    delegate.getPrice(accountInfo, orderBook)
}

case class OneAboveHighestBidStrategy(sellCurrency: Currency) extends TradeStrategy {
  private val delegate = MatchHighestBidStrategy(sellCurrency)
  private val delta = BigDecimal(0.01)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] = {
    val topOpposingOrder = delegate.topOpposingOrderPrice(orderBook)
    delegate.getPrice(accountInfo, orderBook).map(order => topOpposingOrder.min(order + delta))
  }
}
