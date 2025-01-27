package com.taylor.datetransforminator.domain

import org.slf4j.LoggerFactory

case class ErrorResponse(status: Integer, error: String)

object ErrorResponse {

  val logger = LoggerFactory.getLogger(getClass.getName)
  def apply(t: Throwable, status: Integer, error: String) = {
    logger.error("Exception found: " + t.getMessage)
    logger.error(t.getStackTrace.mkString("\n"))
    new ErrorResponse(status, error)
  }
  
  def apply(c: CustomException) = {
    new ErrorResponse(c.status, c.msg)
  }
}