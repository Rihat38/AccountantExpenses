package dao

import cats.syntax.either._
import domain.Errors._
import domain.Fields._
import domain._
import doobie._
import doobie.implicits._

trait CashTransactionSQL {
  def listAll: ConnectionIO[List[CashTransaction]]
  def findById(id: TranId): ConnectionIO[Option[CashTransaction]]
  def removeById(id: TranId): ConnectionIO[Either[TransactionNotFound, Unit]]
  def create(
      cashTransaction: CreateCashTransaction
  ): ConnectionIO[CashTransaction]
}

object CashTransactionSQL {
  object sqls {
    val listAllSql: Query0[CashTransaction] =
      sql"select * from CASH_TRANSACTIONS".query[CashTransaction]

    def findByIdSql(id: TranId): Query0[CashTransaction] =
      sql"select * from CASH_TRANSACTIONS where id=${id.value}"
        .query[CashTransaction]

    def removeByIdSql(id: TranId): Update0 =
      sql"DELETE from CASH_TRANSACTIONS where id=${id.value}".update

    def insertSql(createCashTransaction: CreateCashTransaction): Update0 =
      sql"INSERT INTO CASH_TRANSACTIONS (amount, name, date, source) VALUES (${createCashTransaction.amount.value}, ${createCashTransaction.name.value}, ${createCashTransaction.amount.value}, ${createCashTransaction.amount.value})".update
  }

  private final class Impl extends CashTransactionSQL {
    import sqls._

    override def listAll: ConnectionIO[List[CashTransaction]] =
      listAllSql.to[List]

    override def findById(id: TranId): ConnectionIO[Option[CashTransaction]] =
      findByIdSql(id).option

    override def removeById(
        id: TranId
    ): ConnectionIO[Either[TransactionNotFound, Unit]] =
      removeByIdSql(id).run.map {
        case 0 => TransactionNotFound(id).asLeft[Unit]
        case _ => ().asRight[TransactionNotFound]
      }

    override def create(
        cashTransaction: CreateCashTransaction
    ): ConnectionIO[CashTransaction] =
      insertSql(cashTransaction)
        .withUniqueGeneratedKeys[TranId]("id")
        .map(id =>
          CashTransaction(
            id,
            cashTransaction.amount,
            cashTransaction.name,
            cashTransaction.date,
            cashTransaction.source
          )
        )
  }

  def make: CashTransactionSQL = new Impl
}
