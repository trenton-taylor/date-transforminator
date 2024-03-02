package com.bluechipfinancial.datetransforminator

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = DateTransforminatorServer.run[IO]
