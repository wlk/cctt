package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.data.{CalculatedOrder, ExchangeState}
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.Order.OrderType
import scala.jdk.CollectionConverters._

sealed trait TradeStrategy {
  def getOrder(exchangeInfo: ExchangeState): Option[CalculatedOrder]
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

  def topOpposingOrderPrice(exchangeInfo: ExchangeState): java.math.BigDecimal =
    exchangeInfo.orderBook
      .getOrders(opposingOrderType(orderType))
      .asScala
      .sorted
      .headOption
      .map(_.getLimitPrice)
      .getOrElse(new java.math.BigDecimal(0))
}

case object NoOpTradeStrategy extends TradeStrategy {
  override def getOrder(exchangeInfo: ExchangeState): Option[CalculatedOrder] = None
}

private case class ExactMatchingStrategy(
    maxAmount: BigDecimal,
    sellCurrency: Currency,
    override val orderType: OrderType)
    extends TradeStrategy
    with WithOrderType {

  override def getOrder(exchangeInfo: ExchangeState): Option[CalculatedOrder] = {
    val walletAmount = BigDecimal(exchangeInfo.accountInfo.getWallet.getBalance(sellCurrency).getAvailable)
    val amount = walletAmount.min(maxAmount)

    exchangeInfo.orderBook.getOrders(orderType).asScala.sorted.headOption.map(_.getLimitPrice).flatMap { bestOrder =>
      if (amount == 0 || bestOrder == BigDecimal(0).bigDecimal) {
        None
      } else {
        Some(CalculatedOrder(amount, bestOrder))
      }
    }
  }
}

case class MatchLowestAskStrategy(maxAmount: BigDecimal, sellCurrency: Currency)
    extends TradeStrategy
    with WithOrderType {
  override val orderType = OrderType.ASK
  private val delegate = ExactMatchingStrategy(maxAmount, sellCurrency, orderType)

  override def getOrder(exchangeInfo: ExchangeState): Option[CalculatedOrder] =
    delegate.getOrder(exchangeInfo)
}

case class OneBelowLowestAskStrategy(maxAmount: BigDecimal, sellCurrency: Currency) extends TradeStrategy {
  private val delegate = MatchLowestAskStrategy(maxAmount, sellCurrency)
  private val delta = BigDecimal(0.01)

  override def getOrder(exchangeInfo: ExchangeState): Option[CalculatedOrder] = {
    val topOpposingOrder = delegate.topOpposingOrderPrice(exchangeInfo)
    delegate.getOrder(exchangeInfo).map(order => order.copy(price = topOpposingOrder.max(order.price - delta)))
  }
}

case class MatchHighestBidStrategy(maxAmount: BigDecimal, sellCurrency: Currency)
    extends TradeStrategy
    with WithOrderType {
  override val orderType = OrderType.BID

  private val delegate = ExactMatchingStrategy(maxAmount, sellCurrency, orderType)

  override def getOrder(exchangeInfo: ExchangeState) =
    delegate.getOrder(exchangeInfo)
}

case class OneAboveHighestBidStrategy(maxAmount: BigDecimal, sellCurrency: Currency) extends TradeStrategy {
  private val delegate = MatchHighestBidStrategy(maxAmount, sellCurrency)
  private val delta = BigDecimal(0.01)

  override def getOrder(exchangeInfo: ExchangeState): Option[CalculatedOrder] = {
    val topOpposingOrder = delegate.topOpposingOrderPrice(exchangeInfo)
    delegate.getOrder(exchangeInfo).map(order => order.copy(price = topOpposingOrder.min(order.price + delta)))
  }
}
