package services

import cats.syntax.either._
import cats.effect.IO
import doobie._
import doobie.implicits._
import dao.CashTransactionSQL
import domain.{CashTransaction, CreateCashTransaction}
import domain.Errors.{AppError, InternalError}
import domain.Fields.TranId
import tofu.logging._

trait CashTransactionStorage {
  def list: IO[Either[InternalError, List[CashTransaction]]]
  def findById(id: TranId): IO[Either[InternalError, Option[CashTransaction]]]
  def removeById(id: TranId): IO[Either[AppError, Unit]]
  def create(tran: CreateCashTransaction): IO[CashTransaction]
}

object CashTransactionStorage {
  private final class Impl(sql: CashTransactionSQL, transactor: Transactor[IO])
      extends CashTransactionStorage {
    override def list: IO[Either[InternalError, List[CashTransaction]]] =
      sql.listAll.transact[IO](transactor).attempt.map(_.leftMap(InternalError))

    override def findById(
        id: TranId
    ): IO[Either[InternalError, Option[CashTransaction]]] =
      sql
        .findById(id)
        .transact(transactor)
        .attempt
        .map(_.leftMap(InternalError))

    override def removeById(id: TranId): IO[Either[AppError, Unit]] =
      sql.removeById(id).transact(transactor).attempt.map {
        case Left(th)           => InternalError(th).asLeft[Unit]
        case Right(Left(error)) => error.asLeft[Unit]
        case _                  => ().asRight[AppError]
      }

    override def create(tran: CreateCashTransaction): IO[CashTransaction] =
      sql.create(tran).transact(transactor)
  }

  private final class LoggingImpl(storage: CashTransactionStorage)(implicit
      logging: Logging[IO]
  ) extends CashTransactionStorage {
    override def list: IO[Either[InternalError, List[CashTransaction]]] = {
      for {
        _ <- logging.info("Getting all transactions")
        res <- storage.list
        _ <- res match {
          case Left(error) =>
            logging.error(
              s"Error while getting all transactions: ${error.toString}"
            )
          case Right(_) => logging.info(s"All transactions received")
        }
      } yield res
    }

    override def findById(
        id: TranId
    ): IO[Either[InternalError, Option[CashTransaction]]] = {
      for {
        _ <- logging.info("Getting transaction by id")
        res <- storage.findById(id)
        _ <- res match {
          case Left(error) =>
            logging.error(
              s"Error while getting transaction by id: ${error.toString}"
            )
          case Right(_) => logging.info(s"Transaction received")
        }
      } yield res
    }

    override def removeById(id: TranId): IO[Either[AppError, Unit]] = {
      for {
        _ <- logging.info("Deleting transaction by id")
        res <- storage.removeById(id)
        _ <- res match {
          case Left(error) =>
            logging.error(
              s"Error while deleting transaction by id: ${error.toString}"
            )
          case Right(_) => logging.info(s"Transaction deleted")
        }
      } yield res
    }

    override def create(tran: CreateCashTransaction): IO[CashTransaction] = {
      for {
        _ <- logging.info("Creating transaction")
        res <- storage.create(tran)
      } yield res
    }
  }

  def make(sql: CashTransactionSQL, transactor: Transactor[IO])(implicit
      logging: Logging[IO]
  ): CashTransactionStorage = {
    val storage = new Impl(sql, transactor)
    new LoggingImpl(storage)
  }
}
