package com.wlangiewicz.cctt.http.endpoints.openorders

import com.wlangiewicz.cctt.http.ApiBaseEndpoint
import com.wlangiewicz.cctt.http.data._
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

object OpenOrdersEndpoint extends ApiBaseEndpoint {

  val openOrdersEndpoint: Endpoint[
    AuthToken,
    ApiError,
    OpenOrdersResponse,
    Any
  ] =
    baseEndpoint.get
      .name("Get Open Orders")
      .description("List currently open orders")
      .tag("Orders")
      .in("open-orders")
      .out(jsonBody[OpenOrdersResponse].example(OpenOrdersResponse.example))

}
