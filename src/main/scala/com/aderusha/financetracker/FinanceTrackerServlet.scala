package com.aderusha.financetracker

import org.scalatra._
import com.aderusha.financetracker.dao.Transaction
import com.aderusha.financetracker.dao.FinanceTrackerDao
import org.joda.time.DateTime
import com.aderusha.financetracker.Utility._

class FinanceTrackerServlet(val dao: FinanceTrackerDao) extends FinancetrackerStack {

  get("/create/?") {
    dao.initTables()
  }

  get("/test/?") {
    Transaction(None, parseDate("01/23/2014"), 123, "asdf", "agf34g")
  }

  get("/?") {
    contentType = "text/html"

    val fromDate = parseDate(params.getOrElse("fromDate", dateTimeToString(new DateTime().withDayOfMonth(1))))
    val toDate = parseDate(params.getOrElse("toDate", dateTimeToString(new DateTime())))


    dao.getTransactions(fromDate, toDate) match {
      case Some(transactions) => {
        val pieData: List[(String, Int, String)] = transactions.groupBy(_.category).map { case (k, v) => (k, v.map(_.amount).sum, Utility.createColor()) }.toList
        ssp("/index", "transactions" -> transactions, 
		      "pieData" -> pieData,
		      "fromDate" -> Utility.dateTimeToString(fromDate),
		      "toDate" -> Utility.dateTimeToString(toDate))
      }
      case None => InternalServerError
    }
  }

  get("/add/?") {
    contentType = "text/html"
    val today = Utility.dateTimeToString(new DateTime());
    val categories = dao.getCategories()
    ssp("/add", "today" -> today, "categories" -> categories)
  }

  post("/add/?") {
    val transDate = params("transDate")
    val amount = params("amount")
    val category = if (params("category") == "newCategory") params("newCategory") else params("category")
    val description = params("description")

    val t = if (amount.contains(".")) {
      Transaction(None, parseDate(transDate), (amount.toDouble*100).toInt, description, category)
    } else {
      Transaction(None, parseDate(transDate), amount.toInt, description, category)
    }
    dao.addTransaction(t)

    redirect("/")
  }

  get("/edit/?") {
    "Not Implemented yet"
  }

  post("/edit/?") {
    "Not Implemented yet"
  }

  post("/delete/?") {
    val id = params("id").toLong
    dao.deleteTransaction(id)
    redirect("/")
  }
}
