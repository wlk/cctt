package com.wlangiewicz.cctt.core

import java.util.Date

import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.marketdata.OrderBook
import org.knowm.xchange.dto.trade.LimitOrder

import scala.jdk.CollectionConverters._

object OrderBookTestHelper {
  private val now = new Date(System.currentTimeMillis())

  val empty = new OrderBook(new Date(System.currentTimeMillis()), List[LimitOrder]().asJava, List[LimitOrder]().asJava)

  val singleValue = {
    val asks = List[LimitOrder](
      new LimitOrder(
        OrderType.ASK,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        BigDecimal(4300).bigDecimal
      )
    )
    val bids = List[LimitOrder](
      new LimitOrder(
        OrderType.BID,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        BigDecimal(4200).bigDecimal
      )
    )
    new OrderBook(new Date(System.currentTimeMillis()), asks.asJava, bids.asJava)
  }

  def withTopOrders(lowestAsk: BigDecimal, highestBid: BigDecimal) = {
    val asks = List[LimitOrder](
      new LimitOrder(OrderType.ASK, BigDecimal(1).bigDecimal, CurrencyPair.BTC_USD, "", now, lowestAsk.bigDecimal),
      new LimitOrder(
        OrderType.ASK,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        (lowestAsk + 1).bigDecimal
      ),
      new LimitOrder(
        OrderType.ASK,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        (lowestAsk + 2).bigDecimal
      ),
      new LimitOrder(OrderType.ASK, BigDecimal(1).bigDecimal, CurrencyPair.BTC_USD, "", now, (lowestAsk + 3).bigDecimal)
    )

    val bids = List[LimitOrder](
      new LimitOrder(OrderType.BID, BigDecimal(1).bigDecimal, CurrencyPair.BTC_USD, "", now, highestBid.bigDecimal),
      new LimitOrder(
        OrderType.BID,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        (highestBid - 1).bigDecimal
      ),
      new LimitOrder(
        OrderType.BID,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        (highestBid - 2).bigDecimal
      ),
      new LimitOrder(
        OrderType.BID,
        BigDecimal(1).bigDecimal,
        CurrencyPair.BTC_USD,
        "",
        now,
        (highestBid - 3).bigDecimal
      )
    )

    new OrderBook(new Date(System.currentTimeMillis()), asks.asJava, bids.asJava)
  }

}
