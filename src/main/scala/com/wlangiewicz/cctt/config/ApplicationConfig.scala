package com.wlangiewicz.cctt.config

import com.wlangiewicz.cctt.core._
import org.knowm.xchange.BaseExchange
import org.knowm.xchange.currency.{Currency, CurrencyPair}
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._

object ApplicationConfig {
  private val LoadConfig: Either[ConfigReaderFailures, AppConfig] = ConfigSource.default.load[AppConfig]

  val Config: CCTTConfig = LoadConfig match {
    case Left(err)   => throw new Exception(s"Config error: $err")
    case Right(conf) => CCTTConfig.fromUnsafeConfig(conf.cctt)
  }

  private case class AppConfig(cctt: CCTTUnsafeConfig)

  private case class CCTTUnsafeConfig(
      key: String,
      secret: String,
      sleep: Long,
      sellCurrency: String,
      buyCurrency: String,
      exchange: String,
      maxTradeAmount: BigDecimal,
      tradeStrategy: String)

  case class CCTTConfig(
      key: String,
      secret: String,
      sleep: Long,
      sellCurrency: Currency,
      buyCurrency: Currency,
      exchange: BaseExchange,
      pair: CurrencyPair,
      tradeStrategy: TradeStrategy)

  object CCTTConfig {

    def fromUnsafeConfig(c: CCTTUnsafeConfig): CCTTConfig = {
      val sellCurrency = new Currency(c.sellCurrency)

      CCTTConfig(
        c.key,
        c.secret,
        c.sleep,
        sellCurrency,
        new Currency(c.buyCurrency),
        ExchangeSelector.getExchange(c.exchange),
        new CurrencyPair(c.sellCurrency, c.buyCurrency),
        tradeStrategy(c.tradeStrategy, c.maxTradeAmount, sellCurrency)
      )
    }

    def tradeStrategy(
        tradeStrategyString: String,
        maxTradeAmount: BigDecimal,
        sellCurrency: Currency
      ): TradeStrategy =
      tradeStrategyString match {
        case "NoOpTradeStrategy"         => NoOpTradeStrategy
        case "MatchLowestAskStrategy"    => MatchLowestAskStrategy(maxTradeAmount, sellCurrency)
        case "OneBelowLowestAskStrategy" => OneBelowLowestAskStrategy(maxTradeAmount, sellCurrency)
        case "MatchHighestBidStrategy"   => MatchHighestBidStrategy(maxTradeAmount, sellCurrency)
        case "OneAboveHighestBidStrategy" =>
          OneAboveHighestBidStrategy(maxTradeAmount, sellCurrency)
        case other => throw new InvalidTradeStrategyException(other)
      }

  }

}
