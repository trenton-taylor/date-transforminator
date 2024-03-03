package com.bluechipfinancial.datetransforminator.domain

case class CustomException(status: Integer, msg: String) extends Exception
