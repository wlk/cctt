package com.wlangiewicz.cctt.config

class InvalidSellStrategyException(invalidStrategy: String)
    extends RuntimeException(s"unsupported sell strategy: $invalidStrategy")
