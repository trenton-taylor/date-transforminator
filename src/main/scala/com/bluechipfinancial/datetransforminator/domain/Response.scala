package com.bluechipfinancial.datetransforminator.domain

import com.bluechipfinancial.datetransforminator.{Cfg, FileRequest, TextRequest}

import java.text.SimpleDateFormat
import scala.util.matching.Regex
case class Response(sourceType: String, source: String, fromDate: String, toDate: String, original: String, transformed: String)

val dateToRegexMappings = Map(
  "mm/dd/yyyy" -> "\\d{1,2}\\/\\d{1,2}\\/\\d{2,4}",
  "mm-dd-yyyy" -> "\\d{1,2}\\-\\d{1,2}\\-\\d{2,4}",
  "yyyy/mm/dd" -> "\\d{2,4}\\-\\d{1,2}\\-\\d{1,2}",
)

def transformDates(sourceText: String, fromDateString: String, toDateString: String): String = {

  val regexToFind = dateToRegexMappings.get(fromDateString) match {
    case Some(s) => s
    case None => throw CustomException(400, s"Date format '${fromDateString}' is not supported!")
  }

  val fromDateFormatter = new SimpleDateFormat(fromDateString)
  val toDateFormatter = new SimpleDateFormat(toDateString)

  val regex: Regex = regexToFind.r
  val matches = regex.findAllIn(sourceText)

  val replacements = collection.mutable.Map[String, String]()

  while (matches.hasNext) {
    val oldDateString = matches.next
    val oldDate = fromDateFormatter.parse(oldDateString)
    val newDateString = toDateFormatter.format(oldDate)
    replacements += oldDateString -> newDateString
  }

  replacements.size match {
    case 0 => sourceText // no matches so just return the original string
    case _ => replacements.map((k, v) => sourceText.replace(k, v)).mkString
  }

}

object Response {
  val filePrefix = Cfg().getString("file.folder")

  def apply(request: FileRequest) = {
    val sourceText = scala.io.Source.fromResource(s"${filePrefix}/${request.fileSource}").getLines.mkString
    try {
      val transformedText = transformDates(sourceText, request.fromDate, request.toDate)
      new Response("file", request.fileSource, request.fromDate, request.toDate, sourceText, transformedText)
    } catch {
      case e: Exception => throw CustomException(400, s"Error converting dates in a file: ${e.getMessage}")
    }
  }

  def apply(request: TextRequest) = {
    try {
      val transformedText = transformDates(request.sourceText, request.fromDate, request.toDate)
      new Response("text", request.sourceText, request.fromDate, request.toDate, request.sourceText, transformedText)
    } catch {
      case e: Exception => throw CustomException(400, s"Error converting dates in a text: ${e.getMessage}")
    }
  }
}



