package com.wlangiewicz.cctt.config

import org.knowm.xchange.BaseExchange
import org.knowm.xchange.kraken.KrakenExchange

object ExchangeSelector {

  def getExchange(exchange: String): BaseExchange =
    exchange match {
      case "Kraken" => new KrakenExchange()
      case unknown  => throw new RuntimeException(s"unsupported exchange: $unknown")
    }
}
