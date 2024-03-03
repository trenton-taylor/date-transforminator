package com.bluechipfinancial.datetransforminator

import com.bluechipfinancial.datetransforminator.domain.{CustomException, Response}
import org.scalatest.flatspec.AnyFlatSpec

import java.io.FileNotFoundException

class ResponseSpec extends AnyFlatSpec {

  val validDateFormat01 = "mm/dd/yyyy"
  val validDateFormat02 = "yyyy/mm/dd"

  val invalidDateFormat = "mm-/d/-yyyyy"

  val validFileName = "test01.txt"
  val invalidFileName = "test0aaaaabbbb.txt"

  val validSampleTextNoDate = "Today is today!"
  val validSampleTextWithDate = "Today is 12/31/1999 and tomorrow is 01-01-2000"

  "A request to translate dates for a file request" should "translate dates in the provided file" in {
    val fileRequest = FileRequest(validFileName, validDateFormat01, validDateFormat02)
    val r = Response(fileRequest)
    assert(r.transformed.equals("Today is today!"))
  }

  it should "throw a FileNotFoundException" in {
    val fileRequest = FileRequest("test999999.txt", validDateFormat01, validDateFormat02)
    assertThrows[FileNotFoundException](Response(fileRequest))
  }

  it should "throw a CustomException due to an invalid date format" in {
    val fileRequest = FileRequest("test01.txt", invalidDateFormat, validDateFormat02)
    assertThrows[CustomException](Response(fileRequest))
  }


  "A request to translate dates for a text request" should "translate dates in the provided text" in {
    val textRequest = TextRequest(validSampleTextNoDate, validDateFormat01, validDateFormat02)
    val r = Response(textRequest)
    assert(r.transformed.equals("This is a test record!"))
  }

  it should "throw a custom exception due to an invalid date format" in {
    val textRequest = TextRequest(validSampleTextNoDate, invalidDateFormat, validDateFormat02)
    assertThrows[CustomException](Response(textRequest))
  }



}

