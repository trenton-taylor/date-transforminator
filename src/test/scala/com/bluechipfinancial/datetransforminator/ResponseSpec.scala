package com.bluechipfinancial.datetransforminator

import com.bluechipfinancial.datetransforminator.domain.{CustomException, Response}
import org.scalatest.flatspec.AnyFlatSpec

import java.io.FileNotFoundException

class ResponseSpec extends AnyFlatSpec {

  val validDateFormat01 = "mm/dd/yyyy"
  val validDateFormat02 = "mm-dd-yyyy"
  val validDateFormat03 = "yyyy/mm/dd"
  val invalidDateFormat = "mm-/d/-yyyyy"

  val fileWithNoDate = "test01.txt"
  val fileWithDates = "test02.txt"
  val invalidFileName = "test0aaaaabbbb.txt"

  val sampleTextNoDate = "Today is today!"
  val sampleText1 = "Today is 12/31/1999 and tomorrow is 01-01-2000!"
  val sampleText2 = "Today is 1999/12/31 and tomorrow is 01-01-2000!"

  "A request to translate dates for a file request" should "translate dates in the provided file" in {
    val fileRequest = FileRequest(fileWithNoDate, validDateFormat01, validDateFormat02)
    val r = Response(fileRequest)
    assert(r.transformed.equals("Today is today!"))
  }

  it should "throw a FileNotFoundException" in {
    val fileRequest = FileRequest(invalidFileName, validDateFormat01, validDateFormat02)
    assertThrows[FileNotFoundException](Response(fileRequest))
  }

  it should "throw a CustomException due to an invalid date format" in {
    val fileRequest = FileRequest(fileWithNoDate, invalidDateFormat, validDateFormat02)
    assertThrows[CustomException](Response(fileRequest))
    try{
      Response(fileRequest)
    } catch {
      case e: CustomException => assert(e.msg.equals("Date format 'mm-/d/-yyyyy' is not supported!"))
    }
  }

  it should "test date formatting and translations in a file" in {
    var input = fileWithDates
    var fileRequest = FileRequest(input, validDateFormat01, "yyyy/mm/dd")
    var result = Response(fileRequest)
    assert(result.transformed.equals("Today is 1999/12/31 and tomorrow is 01-01-2000!"))

    fileRequest = FileRequest(input, validDateFormat01, "yy-mm-dd")
    result = Response(fileRequest)
    System.out.println(result.transformed)
    assert(result.transformed.equals("Today is 99-12-31 and tomorrow is 01-01-2000!"))

  }
  
  "A request to translate dates for a text request" should "translate dates in the provided text" in {
    val textRequest = TextRequest(sampleTextNoDate, validDateFormat01, validDateFormat02)
    val result = Response(textRequest)
    assert(result.transformed.equals("Today is today!"))
  }

  it should "throw a custom exception due to an invalid date format" in {
    val textRequest = TextRequest(sampleTextNoDate, invalidDateFormat, validDateFormat02)
    assertThrows[CustomException](Response(textRequest))
    try {
      Response(textRequest)
    } catch {
      case e: CustomException => assert(e.msg.equals("Date format 'mm-/d/-yyyyy' is not supported!"))
    }
  }

  it should "test date formatting and translations in text" in {
    var textRequest = TextRequest(sampleText1, validDateFormat01, "yyyy/mm/dd")
    var result = Response(textRequest)
    assert(result.transformed.equals("Today is 1999/12/31 and tomorrow is 01-01-2000!"))

    textRequest = TextRequest(sampleText2, validDateFormat02, "mm/dd/yyyy")
    result = Response(textRequest)
    assert(result.transformed.equals("Today is 1999/12/31 and tomorrow is 01/01/2000!"))
  }
  
}

