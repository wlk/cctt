package com.wlangiewicz.cctt.config

import com.wlangiewicz.cctt.core._
import org.knowm.xchange.BaseExchange
import org.knowm.xchange.currency.{Currency, CurrencyPair}
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._

object ApplicationConfig {
  private val LoadConfig: Either[ConfigReaderFailures, AppConfig] = ConfigSource.default.load[AppConfig]

  val Config = LoadConfig match {
    case Left(err)   => throw new Exception(s"Config error: $err")
    case Right(conf) => SellbotConfig.fromUnsafeConfig(conf.cctt)
  }

  private case class AppConfig(cctt: SellbotUnsafeConfig)

  private case class SellbotUnsafeConfig(
      key: String,
      secret: String,
      sleep: Long,
      sellCurrency: String,
      buyCurrency: String,
      exchange: String,
      maxSellAmount: BigDecimal,
      sellStrategy: String,
      minSellPrice: BigDecimal)

  case class SellbotConfig(
      key: String,
      secret: String,
      sleep: Long,
      sellCurrency: Currency,
      buyCurrency: Currency,
      exchange: BaseExchange,
      pair: CurrencyPair,
      sellStrategy: SellStrategy,
      minSellPrice: BigDecimal)

  object SellbotConfig {

    def fromUnsafeConfig(c: SellbotUnsafeConfig): SellbotConfig = {
      val sellCurrency = new Currency(c.sellCurrency)

      SellbotConfig(
        c.key,
        c.secret,
        c.sleep,
        sellCurrency,
        new Currency(c.buyCurrency),
        ExchangeSelector.getExchange(c.exchange),
        new CurrencyPair(c.sellCurrency, c.buyCurrency),
        sellStrategy(c.sellStrategy, c.maxSellAmount, sellCurrency),
        c.minSellPrice
      )
    }

    def sellStrategy(
        sellStrategyString: String,
        maxSellAmount: BigDecimal,
        sellCurrency: Currency
      ): SellStrategy =
      sellStrategyString match {
        case "NoOpSellStrategy"           => NoOpSellStrategy
        case "MatchLowestAskStrategy"     => MatchLowestAskStrategy(maxSellAmount, sellCurrency)
        case "OneBelowLowestAskStrategy"  => OneBelowLowestAskStrategy(maxSellAmount, sellCurrency)
        case "MatchHighestBidStrategy"    => MatchHighestBidStrategy(maxSellAmount, sellCurrency)
        case "OneAboveHighestBidStrategy" => OneAboveHighestBidStrategy(maxSellAmount, sellCurrency)
        case other                        => throw new InvalidSellStrategyException(other)
      }

  }

}
