package com.wlangiewicz.cctt.http
import java.util.UUID

import com.wlangiewicz.cctt.http.data._
import sttp.tapir.SchemaType.SNumber
import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema, SchemaType, Validator}

import scala.util.{Failure, Success, Try}

trait EnumHelper { e: Enumeration =>
  import io.circe._

  implicit val enumDecoder: Decoder[e.Value] = Decoder.decodeEnumeration(e)
  implicit val enumEncoder: Encoder[e.Value] = Encoder.encodeEnumeration(e)
  implicit val enumCodecPlain: sttp.tapir.Codec[String, Value, CodecFormat.TextPlain] = sttp.tapir.Codec.string.map(e.withName _)(_.toString)

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
}

trait ApiJsonFormats {
  // this will encode/decode objects as direct json values
}
