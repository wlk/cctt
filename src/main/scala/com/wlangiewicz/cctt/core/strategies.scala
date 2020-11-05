package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.config.InvalidTradeStrategyException
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.marketdata.OrderBook

import scala.jdk.CollectionConverters._
import scala.util.Try

sealed trait TradeStrategy {
  def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal]
}

object TradeStrategy {

  def create(tradeStrategyString: String): Try[TradeStrategy] =
    Try(tradeStrategyString match {
      case "NoOpTradeStrategy"         => NoOpTradeStrategy
      case "MatchLowestAskStrategy"    => MatchLowestAskStrategy()
      case "OneBelowLowestAskStrategy" => OneBelowLowestAskStrategy()
      case "MatchHighestBidStrategy"   => MatchHighestBidStrategy()
      case "OneAboveHighestBidStrategy" =>
        OneAboveHighestBidStrategy()
      case other => throw new InvalidTradeStrategyException(other)
    })

}

sealed private[core] trait WithOrderType {

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

private case class ExactMatchingStrategy(override val orderType: OrderType) extends TradeStrategy with WithOrderType {

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] =
    orderBook.getOrders(orderType).asScala.sorted.headOption.map(_.getLimitPrice).flatMap { bestPrice =>
      if (bestPrice == BigDecimal(0).bigDecimal) {
        None
      } else {
        Some(bestPrice)
      }
    }
}

case class MatchLowestAskStrategy() extends TradeStrategy with WithOrderType {
  override val orderType = OrderType.ASK
  private val delegate = ExactMatchingStrategy(orderType)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] =
    delegate.getPrice(accountInfo, orderBook)
}

case class OneBelowLowestAskStrategy() extends TradeStrategy {
  private val delegate = MatchLowestAskStrategy()
  private val delta = BigDecimal(0.01)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] = {
    val topOpposingOrder = delegate.topOpposingOrderPrice(orderBook)
    delegate.getPrice(accountInfo, orderBook).map(order => topOpposingOrder.max(order - delta))
  }
}

case class MatchHighestBidStrategy() extends TradeStrategy with WithOrderType {
  override val orderType = OrderType.BID

  private val delegate = ExactMatchingStrategy(orderType)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] =
    delegate.getPrice(accountInfo, orderBook)
}

case class OneAboveHighestBidStrategy() extends TradeStrategy {
  private val delegate = MatchHighestBidStrategy()
  private val delta = BigDecimal(0.01)

  override def getPrice(accountInfo: AccountInfo, orderBook: OrderBook): Option[BigDecimal] = {
    val topOpposingOrder = delegate.topOpposingOrderPrice(orderBook)
    delegate.getPrice(accountInfo, orderBook).map(order => topOpposingOrder.min(order + delta))
  }
}
