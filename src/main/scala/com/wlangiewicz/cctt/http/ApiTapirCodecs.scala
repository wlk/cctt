package com.wlangiewicz.cctt.http

import java.util.UUID

import com.wlangiewicz.cctt.core.TradeStrategy
import com.wlangiewicz.cctt.http.data._
import io.circe.Decoder
import org.knowm.xchange.currency.CurrencyPair
import sttp.tapir.SchemaType.{SNumber, SString}
import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema, SchemaType, Validator}

import scala.util.{Failure, Success, Try}

trait EnumHelper { e: Enumeration =>
  import io.circe._

  implicit val enumDecoder: Decoder[e.Value] = Decoder.decodeEnumeration(e)
  implicit val enumEncoder: Encoder[e.Value] = Encoder.encodeEnumeration(e)

  implicit val enumCodecPlain: sttp.tapir.Codec[String, Value, CodecFormat.TextPlain] =
    sttp.tapir.Codec.string.map(e.withName _)(_.toString)

  implicit val schemaForEnum: Schema[e.Value] = Schema(SchemaType.SString)
  implicit def validatorForEnum: Validator[e.Value] = Validator.`enum`(e.values.toList, v => Option(v))
}

trait ApiTapirCodecs {
  implicit val AuthTokenCodec: Codec[String, AuthToken, CodecFormat.TextPlain] = Codec.string.map(AuthToken)(_.value)

  implicit val UUIDCodec: Codec[String, UUID, CodecFormat.TextPlain] = {
    def decode(value: String): DecodeResult[UUID] = Try(UUID.fromString(value)) match {
      case Success(s)  => DecodeResult.Value(s)
      case Failure(ex) => DecodeResult.Error(value, ex)
    }
    Codec.string.mapDecode(decode)(_.toString)
  }
  implicit val schemaForBigDecimal: Schema[BigDecimal] = Schema(SNumber).format("decimal")
  implicit val schemaForCurrencyPair: Schema[CurrencyPair] = Schema(SString)
  implicit val schemaForTradeStrategy: Schema[TradeStrategy] = Schema(SString)

  implicit val TradeIdCodec = Codec.uuid.map(TradeId.apply _)(_.value)
}

trait ApiJsonFormats {
  import io.circe.Encoder

  // this will encode/decode objects as direct json values
  implicit val TradeIdEncoder: Encoder[TradeId] = Encoder.encodeUUID.contramap[TradeId](_.value)

  implicit val TradeStrategyEncoder: Encoder[TradeStrategy] =
    Encoder.encodeString.contramap[TradeStrategy](_.getClass.getSimpleName)
  implicit val CurrencyPairEncoder: Encoder[CurrencyPair] = Encoder.encodeString.contramap[CurrencyPair](_.toString)

  implicit val TradeIdDecoder: Decoder[TradeId] = Decoder.decodeUUID.emapTry(uuid => Try(TradeId(uuid)))
  implicit val TradeStrategyDecoder: Decoder[TradeStrategy] = Decoder.decodeString.emapTry(s => TradeStrategy.create(s))
  implicit val CurrencyPairDecoder: Decoder[CurrencyPair] = Decoder.decodeString.emapTry(s => Try(new CurrencyPair(s)))

}
