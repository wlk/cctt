package com.wlangiewicz.cctt.http.data

import com.wlangiewicz.cctt.http.EnumHelper

object OrderType extends Enumeration with EnumHelper {
  type OrderType = Value
  val Bid, Ask = Value
}
