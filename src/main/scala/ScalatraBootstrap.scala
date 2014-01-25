import com.aderusha.financetracker._
import com.aderusha.financetracker.dao.FinanceTrackerDao
import org.scalatra._
import javax.servlet.ServletContext
import com.mchange.v2.c3p0.ComboPooledDataSource
import scala.slick.session.Database
import org.apache.log4j.Logger

class ScalatraBootstrap extends LifeCycle {
  val logger = Logger.getLogger(classOf[ScalatraBootstrap])

  val cpds = new ComboPooledDataSource
  logger.info("Created c3p0 connection pool")

  override def init(context: ServletContext) {
    val db = Database.forDataSource(cpds)
    val dao = new FinanceTrackerDao(db)
    context.mount(new FinanceTrackerServlet(dao), "/*")
  }

  private def closeDbConnection() {
    logger.info("Closing c3p0 connection pool")
    cpds.close
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }
}
