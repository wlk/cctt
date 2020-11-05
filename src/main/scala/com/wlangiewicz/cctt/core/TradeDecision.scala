package com.wlangiewicz.cctt.core

sealed trait TradeDecision

case object Hold extends TradeDecision
case object Create extends TradeDecision
case object Cancel extends TradeDecision
