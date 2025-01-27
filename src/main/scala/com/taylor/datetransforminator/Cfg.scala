package com.taylor.datetransforminator

import com.typesafe.config.{Config, ConfigFactory}

val config: Config = ConfigFactory.load()
class Cfg {

  def getString(key: String) = config.getString(key)

  def getInt(key: String) = config.getInt(key)

}
