package com.bluechipfinancial.datetransforminator

import cats.effect.Concurrent
import cats.syntax.all.*
import io.circe.{Encoder, Decoder}
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe.*
import org.http4s.Method.*

trait Regex[F[_]]:
  def get: F[Regex.Pattern]

object Regex:
  def apply[F[_]](using ev: Regex[F]): Regex[F] = ev

  final case class Pattern(pattern: String)
  
  object Pattern:
    given Decoder[Pattern] = Decoder.derived[Pattern]
    given [F[_]: Concurrent]: EntityDecoder[F, Pattern] = jsonOf
    given Encoder[Pattern] = Encoder.AsObject.derived[Pattern]
    given [F[_]]: EntityEncoder[F, Pattern] = jsonEncoderOf

  final case class PatternError(e: Throwable) extends RuntimeException

  def impl[F[_]: Concurrent](C: Client[F]): Regex[F] = new Regex[F]:
    val dsl = new Http4sClientDsl[F]{}
    import dsl.*
    def get: F[Regex.Pattern] = 
      C.expect[Pattern](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError{ case t => PatternError(t)} // Prevent Client Json Decoding Failure Leaking