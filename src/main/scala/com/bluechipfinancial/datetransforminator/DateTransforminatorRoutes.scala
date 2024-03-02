package com.bluechipfinancial.datetransforminator

import cats.effect.Sync
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object DateTransforminatorRoutes:
  
  def dateRoutes[F[_]: Sync](H: Service[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "date" / name =>
        for {
          greeting <- H.handle(Service.Request(name))
          resp <- Ok(greeting)
        } yield resp
    }
    
//    def regexRoutes[F[_] : Sync](R: Regex[F]): HttpRoutes[F] =
//      val dsl = new Http4sDsl[F] {}
//      import dsl.*
//      HttpRoutes.of[F] {
//        case GET -> Root / "regex" =>
//          for {
//            regex <- R.get
//            resp <- Ok(regex)
//          } yield resp
//      }