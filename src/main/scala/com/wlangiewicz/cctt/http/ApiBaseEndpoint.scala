package com.wlangiewicz.cctt.http
import com.wlangiewicz.cctt.http.data._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._

trait ApiBaseEndpoint extends ApiTapirCodecs with ApiJsonFormats {

  val baseEndpoint: Endpoint[AuthToken, ApiError, Unit, Any] =
    endpoint
      .in("v1")
      .in(auth.bearer[AuthToken])
      .errorOut(
        oneOf[ApiError](
          statusMapping(StatusCode.NotFound, jsonBody[NotFound].description("Resource Not Found")),
          statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("Unauthorized")),
          statusMapping(StatusCode.InternalServerError, jsonBody[ServerError].description("Internal Server Error"))
        )
      )
}
