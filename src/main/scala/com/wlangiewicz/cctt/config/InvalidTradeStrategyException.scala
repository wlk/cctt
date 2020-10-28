package com.wlangiewicz.cctt.config

class InvalidTradeStrategyException(invalidStrategy: String)
    extends RuntimeException(s"unsupported trade strategy: $invalidStrategy")
