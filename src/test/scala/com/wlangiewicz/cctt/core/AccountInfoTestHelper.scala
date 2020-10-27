package com.wlangiewicz.cctt.core

import java.math.{BigDecimal => JBigDecimal}

import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.account.{AccountInfo, Balance, Wallet}

import scala.jdk.CollectionConverters._

object AccountInfoTestHelper {
  val builder = Wallet.Builder.from(List(new Balance(Currency.USD, new JBigDecimal(0))).asJava)
  val empty = new AccountInfo(builder.build()) //new Wallet("id", "name", new Balance(Currency.USD, new JBigDecimal(0))))

  val hasBtcAndUsd = {
    val wallet = Wallet.Builder
      .from(
        List(
          new Balance(Currency.USD, new JBigDecimal(1000)),
          new Balance(Currency.BTC, new JBigDecimal(2)),
          new Balance(Currency.ETH, new JBigDecimal(20))
        ).asJava
      )
      .build()

    new AccountInfo(wallet)
  }

}
