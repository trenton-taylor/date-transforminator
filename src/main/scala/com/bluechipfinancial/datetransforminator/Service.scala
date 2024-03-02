package com.bluechipfinancial.datetransforminator

import cats.Applicative
import cats.syntax.all.*
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.*

trait Service[F[_]]:
  def handle(request: Service.Request): F[Service.Response]

object Service:
  final case class Request(parameter: String) extends AnyVal
  final case class Response(body: String) extends AnyVal
  
  object Response:
    given Encoder[Response] = new Encoder[Response]:
      final def apply(r: Response): Json = Json.obj(
        ("message", Json.fromString(r.body)),
      )

    given [F[_]]: EntityEncoder[F, Response] =
      jsonEncoderOf[F, Response]

  def impl[F[_]: Applicative]: Service[F] = new Service[F]:
    def handle(request: Service.Request): F[Service.Response] =
        Response("Hello, " + request.parameter).pure[F]
