package com.wlangiewicz.cctt.http.data

sealed trait ApiError

case class NotFound(value: String) extends ApiError
case class Unauthorized(value: String) extends ApiError
case class ServerError(value: String) extends ApiError
case class Unknown(value: String) extends ApiError
