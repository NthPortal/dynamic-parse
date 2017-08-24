package com.nthportal.example.convert

import com.nthportal.convert.Convert

object BooleanParser {
  def parseStrictThrowing(s: String): Boolean = {
    s.toLowerCase match {
      case "true" => true
      case "false" => false
      case _ => throw new IllegalArgumentException(s"'$s' is not 'true' or 'false'")
    }
  }

  def parseStrict(s: String)(implicit c: Convert = Convert.Valid): c.Result[Boolean] = {
    c.conversion {
      s.toLowerCase match {
        case "true" => true
        case "false" => false
        case _ => c.fail(new IllegalArgumentException(s"'$s' is not 'true' or 'false'"))
      }
    }
  }
}
