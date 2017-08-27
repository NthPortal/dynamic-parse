package com.nthportal.example.convert

import com.nthportal.convert.Convert

object CoordinateParser {
  private val parenthesesPattern = """\(.*\)""".r.pattern
  private val separatorRegex = """,\s*""".r

  def parseCoordinateThrowing(s: String): (Int, Int) = {
    try {
      require(parenthesesPattern.matcher(s).matches, "coordinate is not wrapped in parentheses")
      val stripped = s.substring(1, s.length - 1)

      separatorRegex split stripped map { _.toInt } match {
        case Array(x, y) => (x, y)
        case a => throw new IllegalArgumentException("wrong number of values: expected 2, found " + a.length)
      }
    } catch {
      case e: IllegalArgumentException => throw new IllegalArgumentException("invalid coordinate string: " + s, e)
    }
  }

  def parseCoordinate(s: String)(implicit c: Convert): c.Result[(Int, Int)] = {
    c.conversion {
      try {
        c.require(parenthesesPattern.matcher(s).matches, "coordinate is not wrapped in parentheses")
        val stripped = s.substring(1, s.length - 1)

        separatorRegex split stripped map { elem => c.unwrap(parseInt(elem)(c)) } match {
          case Array(x, y) => (x, y)
          case a => c.fail(new IllegalArgumentException("wrong number of values: expected 2, found " + a.length))
        }
      } catch {
        case e: IllegalArgumentException => c.fail(new IllegalArgumentException("invalid coordinate string: " + s, e))
      }
    }
  }

  private def parseInt(s: String)(implicit c: Convert): c.Result[Int] = {
    c.conversion { c.wrapException[NumberFormatException, Int](s.toInt) }
  }
}
