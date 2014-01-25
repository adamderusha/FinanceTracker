package com.aderusha.financetracker.dao

import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession
import scala.slick.session.Database
import java.sql.Date

case class Transaction(id: Option[Long], transDate: Date, amount: Int, description: String, category: String)

object Transactions extends Table[Transaction]("transactions") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def transDate = column[Date]("trans_date")
  def amount = column[Int]("amount")
  def description = column[String]("description")
  def category = column[String]("category")
  
  def * = id.? ~ transDate ~ amount ~ description ~ category <> (Transaction, Transaction.unapply _)
  def autoInc = transDate ~ amount ~ description ~ category returning id

  def add(t: Transaction): Long = {
    Transactions.autoInc.insert(t.transDate, t.amount, t.description, t.category)
  }
}

class FinanceTrackerDao(val db: Database) {
  private def sortDateDesc(date: Date): Long = {
    -(date.getTime())
  }

  def initTables() {
    db withSession {
      Transactions.ddl.create
    }
  }

  def getCategories(): List[String] = {
    var categories: List[String] = List()
    db withSession {
      categories = Query(Transactions).map(_.category).list.distinct
    }
    categories
  }

  def getAllTransactions(): Option[List[Transaction]] = {
    var transactions: Option[List[Transaction]] = None
    db withSession {
      val rawQuery = Query(Transactions)
      val sortedQuery = rawQuery.sortBy(_.transDate.desc)
      transactions = Some(sortedQuery.list)

    }
    transactions
  }

  def getTransactions(fromDate: Date, toDate: Date): Option[List[Transaction]] = {
    var transactions: Option[List[Transaction]] = None
    db withSession {
      val rawQuery = Query(Transactions)
      val filteredQuery = rawQuery.filter(t => t.transDate <= toDate && t.transDate >= fromDate)
      val sortedQuery = filteredQuery.sortBy(_.transDate.desc)
      transactions = Some(sortedQuery.list)
    }
    transactions
  }

  def addTransaction(transaction: Transaction) {
    db withSession {
      Transactions.add(transaction)
    }
  }

  def deleteTransaction(id: Long) {
    db withSession {
      Query(Transactions).filter(_.id === id).delete
    }
  }
}
