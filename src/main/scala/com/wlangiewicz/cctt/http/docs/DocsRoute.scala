package com.wlangiewicz.cctt.http.docs

import akka.http.scaladsl.server.{Directives, Route}
import com.wlangiewicz.cctt.http.ApiRoute
import com.wlangiewicz.cctt.http.endpoints.gettrade.GetTradeEndpoint
import com.wlangiewicz.cctt.http.endpoints.newtrade.NewTradeEndpoint
import com.wlangiewicz.cctt.http.endpoints.openorders.OpenOrdersEndpoint
import sttp.tapir.Endpoint
import sttp.tapir.docs.openapi.RichOpenAPIEndpoints
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.openapi.{Contact, Info}
import sttp.tapir.redoc.akkahttp.RedocAkkaHttp

class DocsRoute extends ApiRoute {
  private val title = "CCTT API"

  private val endpoints: List[Endpoint[_, _, _, _]] =
    List(OpenOrdersEndpoint.openOrdersEndpoint, NewTradeEndpoint.newTradeEndpoint, GetTradeEndpoint.getTradeEndpoint)

  private val apiInfo = Info(
    title = "CCTT API",
    version = "1.0",
    description = Some("Crypto Currency Trading Terminal API"),
    contact = Some(Contact(email = None, url = None, name = None))
  )

  private val docsAsYaml: String = endpoints
    .toOpenAPI(apiInfo)
    .toYaml

  private val docsRoute = new RedocAkkaHttp(title, docsAsYaml, "docs.yaml", "2.0.0-rc.45").routes

  override val route: Route = Directives.pathPrefix("docs")(docsRoute)
}
