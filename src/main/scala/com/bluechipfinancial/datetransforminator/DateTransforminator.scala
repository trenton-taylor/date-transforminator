package com.bluechipfinancial.datetransforminator

import cats.data.OptionT
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import cats.effect.*
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.*
import org.http4s.server.middleware.ErrorAction
import org.http4s.server.middleware.ErrorHandling
import org.slf4j.LoggerFactory

import java.io.FileNotFoundException
import java.text.SimpleDateFormat

case class TextRequest(sourceText: String, fromDate: String, toDate: String)

case class FileRequest(fileSource: String, fromDate: String, toDate: String)

case class Response(sourceType: String, source: String, fromDate: String, toDate: String, original: String, transformed: String)


def transformDates(sourceText: String, fromDateString: String, toDateString: String): String = {
  val fromDate = new SimpleDateFormat(fromDateString)
  val toDate = new SimpleDateFormat(toDateString)
  
}

val config: Config = ConfigFactory.load();

object Response {

  val filePrefix = config.getString("file.folder")
  def apply(request: FileRequest) = {
    val sourceText = scala.io.Source.fromResource(s"${filePrefix}/${request.fileSource}").getLines.mkString
    val transformedText = transformDates(sourceText, request.fromDate, request.toDate)
    new Response("file", request.fileSource, request.fromDate, request.toDate, sourceText, transformedText)
  }

  def apply(request: TextRequest) = {
    val transformedText = transformDates(request.sourceText, request.fromDate, request.toDate)
    new Response("text", request.sourceText, request.fromDate, request.toDate, request.sourceText, transformedText)
  }
}

case class ErrorResponse(status: Integer, error: String)

object ErrorResponse {

  val logger = LoggerFactory.getLogger(getClass.getName)
  def apply(t: Throwable, status: Integer, error: String) = {
    logger.error("Exception found: " + t.getMessage)
    new ErrorResponse(status, error)
  }
}

object DateTransforminator extends IOApp {

  val internalServerError = ErrorResponse(500, "Internal Server Error.")
  val notFoundError = ErrorResponse(404, "Resource not found.")

  // converts the JSON request string to a request case class
  implicit val textRequestDecoder: EntityDecoder[IO, TextRequest] = jsonOf[IO, TextRequest]
  implicit val fileRequestDecoder: EntityDecoder[IO, FileRequest] = jsonOf[IO, FileRequest]

  // converts the response case class to a response JSON string
  implicit val responseEncoder: EntityEncoder[IO, Response] = jsonEncoderOf[IO, Response]
  implicit val errorResponseEncoder: EntityEncoder[IO, ErrorResponse] = jsonEncoderOf[IO, ErrorResponse]

  def errorHandler(t: Throwable, msg: => String): OptionT[IO, Unit] =
    OptionT.liftF(
      IO.println(msg) >>
        IO.println(t) >>
        IO(t.printStackTrace())
    )

  val dateRoutes =
    HttpRoutes.of[IO] {
      case request@POST -> Root / "date" / "transform" / "text" =>
           for {
             r <- request.as[TextRequest]
             resp <- {
               try{
                 Ok(Response(r).asJson)
               } catch {
                 case e: Exception => Ok(ErrorResponse(e, 500, "Internal Server Error").asJson)
               }
             }
           } yield resp

      case request@POST -> Root / "date" / "transform" / "file" =>
        for {
          r <- request.as[FileRequest]
          resp <- {
            try{
              Ok(Response(r).asJson)
            } catch {
              case e: FileNotFoundException => Ok(ErrorResponse(e, 400, "File " + r.fileSource + " was not found!").asJson)
              case e: Exception => Ok(ErrorResponse(e, 500, "Internal Server Error").asJson)
            }
          }
        } yield resp
    }

  val healthCheckRoutes = {
    HttpRoutes.of[IO] {
      case GET -> Root / "health" => Ok("ok")
      case _ => Ok(notFoundError.asJson)
    }
  }

  val httpAppWithDefaultErrorLogging = ErrorHandling.Recover.total(
    ErrorAction.log(
      {dateRoutes <+> healthCheckRoutes},
      messageFailureLogAction = errorHandler,
      serviceErrorLogAction = errorHandler
    )
  )

  val serverHost = config.getString("server.host")
  val serverPort = config.getInt("server.port")
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString(serverHost).get)
      .withPort(Port.fromInt(serverPort).get)
      .withHttpApp(httpAppWithDefaultErrorLogging.orNotFound)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
