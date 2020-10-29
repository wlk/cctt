package com.wlangiewicz.cctt.config

import com.wlangiewicz.cctt.http.EnumHelper

object ExchangeName extends Enumeration with EnumHelper {
  type ExchangeName = Value
  val Kraken, Random, Noop = Value
}
