package com.taylor.datetransforminator.domain

case class CustomException(status: Integer, msg: String) extends Exception
