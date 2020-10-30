package com.wlangiewicz.cctt.config

class InvalidTradeStrategyException(invalidStrategy: String)
    extends RuntimeException(s"Unsupported trade strategy: $invalidStrategy")
