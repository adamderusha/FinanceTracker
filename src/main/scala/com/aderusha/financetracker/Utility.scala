package com.aderusha.financetracker

import java.util.Random
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.sql.Date


object Utility {
  implicit def dateTimeToSQLDate(dateTime: DateTime): Date = {
    new Date(dateTime.getMillis())
  }

  implicit def sqlDateToDateTime(date: Date): DateTime = {
    new DateTime(date.getTime())
  }

  private val r = new Random
  private val formatter = DateTimeFormat.forPattern("MM/dd/YYYY")
  private val colorDigits = List("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")

  private def pickDigits() = for (i <- 1 to 6) yield colorDigits(r.nextInt(colorDigits.size))

  def createColor(): String = {
    val digits = pickDigits()
    "#" + digits.mkString("")
  }

  def parseDate(dateString: String): DateTime = {
    formatter.parseDateTime(dateString)
  }

  def dateTimeToString(dateTime: DateTime): String = {
    formatter.print(dateTime)
  }
}
