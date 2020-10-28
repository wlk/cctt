package com.wlangiewicz.cctt.core

import java.util.Date

import com.typesafe.scalalogging.LazyLogging
import com.wlangiewicz.cctt.data.OrderId
import org.knowm.xchange.currency.{Currency, CurrencyPair}
import org.knowm.xchange.dto.account.{AccountInfo, Balance, Wallet}
import org.knowm.xchange.dto.marketdata.OrderBook
import org.knowm.xchange.dto.trade.LimitOrder

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

sealed trait BaseExchangeIo {
  def getWallet: Wallet
  def getBalance(currency: Currency): Balance
  def getOrderBook(currencyPair: CurrencyPair): OrderBook
  def placeLimitOrder(order: LimitOrder): Option[OrderId]
  def cancelLimitOrder(orderId: OrderId): Option[OrderId]
  def cancelLimitOrder(orderId: OrderId, currencyPair: CurrencyPair): Option[OrderId]
  def getOpenOrders(currencyPair: CurrencyPair): List[LimitOrder]
  def getOpenOrders: List[LimitOrder]
  def getAccountInfo: AccountInfo
}

class NoOpExchangeIo(exchange: BaseExchangeWrapper) extends BaseExchangeIo with LazyLogging {

  override def getWallet: Wallet = {
    logger.debug("NoOpExchangeIo.getWallet")
    exchange.accountService.getAccountInfo.getWallet
  }

  override def getBalance(currency: Currency): Balance = {
    logger.debug("NoOpExchangeIo.getBalance")
    exchange.accountService.getAccountInfo.getWallet.getBalance(currency)
  }

  override def getOrderBook(currencyPair: CurrencyPair): OrderBook = {
    logger.debug("ExchangeIo.getOrderBook")
    exchange.marketService
      .getOrderBook(currencyPair)
  }

  override def placeLimitOrder(order: LimitOrder): Option[OrderId] = {
    logger.debug(s"NoOpExchangeIo.placeLimitOrder: $order")
    Some(OrderId(order.getId))
  }

  override def cancelLimitOrder(orderId: OrderId): Option[OrderId] = {
    logger.debug(s"NoOpExchangeIo.cancelLimitOrder: $orderId")
    Some(orderId)
  }

  override def cancelLimitOrder(orderId: OrderId, currencyPair: CurrencyPair): Option[OrderId] = {
    logger.debug(s"NoOpExchangeIo.cancelLimitOrder: $orderId, $currencyPair")
    Some(orderId)
  }

  override def getOpenOrders(currencyPair: CurrencyPair): List[LimitOrder] = {
    logger.debug("NoOpExchangeIo.getOpenOrders")
    exchange.tradeService.getOpenOrders.getOpenOrders.asScala
      .filter(_.getInstrument == currencyPair)
      .toList
  }

  override def getOpenOrders: List[LimitOrder] = {
    logger.debug("NoOpExchangeIo.getOpenOrders")
    exchange.tradeService.getOpenOrders.getOpenOrders.asScala.toList
  }

  override def getAccountInfo: AccountInfo = exchange.accountService.getAccountInfo
}

class ExchangeIo(exchange: BaseExchangeWrapper) extends BaseExchangeIo with LazyLogging {

  override def getWallet: Wallet = {
    logger.debug("ExchangeIo.getWallet")
    exchange.accountService.getAccountInfo.getWallet
  }

  override def getBalance(currency: Currency): Balance = {
    logger.debug("ExchangeIo.getBalance")
    exchange.accountService.getAccountInfo.getWallet.getBalance(currency)
  }

  override def getOrderBook(currencyPair: CurrencyPair): OrderBook = {
    logger.debug("ExchangeIo.getOrderBook")
    exchange.marketService.getOrderBook(currencyPair)
  }

  override def placeLimitOrder(order: LimitOrder): Option[OrderId] = {
    logger.debug(s"ExchangeIo.placeLimitOrder: $order")
    Try(exchange.tradeService.placeLimitOrder(order)) match {
      case Success(s) => Some(OrderId(s))
      case Failure(ex) =>
        logger.warn(s"Exception creating $order: ${ex.getMessage}")
        None
    }
  }

  override def cancelLimitOrder(orderId: OrderId, currencyPair: CurrencyPair): Option[OrderId] = {
    logger.debug(s"ExchangeIo.cancelLimitOrder: $orderId, currencyPair: $currencyPair")
    Try(exchange.tradeService.cancelOrder(orderId.value)) match {
      case Success(true)  => Some(orderId)
      case Success(false) => None
      case Failure(ex) =>
        logger.warn(s"Exception canceling $orderId: ${ex.getMessage}")
        None
    }
  }

  override def cancelLimitOrder(orderId: OrderId): Option[OrderId] = {
    logger.debug(s"ExchangeIo.cancelLimitOrder: $orderId")
    Try(exchange.tradeService.cancelOrder(orderId.value)) match {
      case Success(true)  => Some(orderId)
      case Success(false) => None
      case Failure(ex) =>
        logger.warn(s"Exception canceling $orderId: ${ex.getMessage}")
        None
    }
  }

  override def getOpenOrders(currencyPair: CurrencyPair): List[LimitOrder] = {
    logger.debug(s"ExchangeIo.getOpenOrders: $currencyPair")
    exchange.tradeService.getOpenOrders.getOpenOrders.asScala
      .filter(_.getInstrument == currencyPair)
      .toList
  }

  override def getOpenOrders: List[LimitOrder] = {
    logger.debug("NoOpExchangeIo.getOpenOrders")
    exchange.tradeService.getOpenOrders.getOpenOrders.asScala.toList
  }

  override def getAccountInfo: AccountInfo = exchange.accountService.getAccountInfo
}

/**
  * Implementation is incomplete, but leaving it for now
  * YAGNI?
  */
class RandomExchangeIo() extends BaseExchangeIo with LazyLogging {

  override def getWallet: Wallet = {
    logger.debug("RandomExchangeIo.getWallet")
    Wallet.Builder.from(List.empty.asJava).build()
  }

  override def getBalance(currency: Currency): Balance = {
    logger.debug("RandomExchangeIo.getBalance")
    Balance.zero(Currency.BTC)
  }

  override def getOrderBook(currencyPair: CurrencyPair): OrderBook = {
    logger.debug("ExchangeIo.getOrderBook")
    new OrderBook(new Date(System.currentTimeMillis), List.empty[LimitOrder].asJava, List.empty[LimitOrder].asJava)
  }

  override def placeLimitOrder(order: LimitOrder): Option[OrderId] = {
    logger.debug(s"RandomExchangeIo.placeLimitOrder: $order")
    None
  }

  override def cancelLimitOrder(orderId: OrderId): Option[OrderId] = {
    logger.debug(s"RandomExchangeIo.cancelLimitOrder: $orderId")
    None
  }

  override def cancelLimitOrder(orderId: OrderId, currencyPair: CurrencyPair): Option[OrderId] = {
    logger.debug(s"RandomExchangeIo.cancelLimitOrder: $orderId, $currencyPair")
    None
  }

  override def getOpenOrders(currencyPair: CurrencyPair): List[LimitOrder] = {
    logger.debug("RandomExchangeIo.getOpenOrders")
    List.empty
  }

  override def getOpenOrders: List[LimitOrder] = {
    logger.debug("RandomExchangeIo.getOpenOrders")
    List.empty
  }

  override def getAccountInfo: AccountInfo = new AccountInfo()
}
