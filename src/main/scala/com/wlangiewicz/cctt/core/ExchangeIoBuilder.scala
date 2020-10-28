package com.wlangiewicz.cctt.core

import com.wlangiewicz.cctt.config.ExchangeName
import org.knowm.xchange.kraken.KrakenExchange

object ExchangeIoBuilder {

  def build(
      exchangeName: ExchangeName.Value,
      key: Option[String],
      secret: Option[String]
    ): BaseExchangeIo =
    (exchangeName, key, secret) match {
      case (ExchangeName.Kraken, Some(k), Some(v)) =>
        val e = new ExchangeWrapper(new KrakenExchange(), k, v)
        new ExchangeIo(e)
      case (ExchangeName.Noop, Some(k), Some(v)) =>
        val e = new ExchangeWrapper(new KrakenExchange(), k, v)
        new NoOpExchangeIo(e)
      case (ExchangeName.Random, _, _) =>
        new RandomExchangeIo()
      case (e, _, _) => throw new RuntimeException(s"Exchange $e not supported")
    }
}
