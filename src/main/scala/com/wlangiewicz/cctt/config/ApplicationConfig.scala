package com.wlangiewicz.cctt.config

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
      key: Option[String],
      secret: Option[String],
      sleep: Long,
      exchange: String)

  case class CCTTConfig(
      key: Option[String],
      secret: Option[String],
      sleep: Long,
      exchange: ExchangeName.Value)

  object CCTTConfig {

    def fromUnsafeConfig(c: CCTTUnsafeConfig): CCTTConfig =
      CCTTConfig(
        c.key,
        c.secret,
        c.sleep,
        ExchangeName.withName(c.exchange)
      )
  }

}
