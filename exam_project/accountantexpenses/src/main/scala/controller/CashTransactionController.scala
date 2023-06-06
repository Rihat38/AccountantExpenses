package controller

import cats.syntax.either._
import domain._
import domain.Errors._
import cats.effect.IO
import services.CashTransactionStorage
import sttp.tapir.server.ServerEndpoint
import Endpoints._


trait CashTransactionController {
  def listAllTransactions: ServerEndpoint[Any, IO]
  def findTransactionById: ServerEndpoint[Any, IO]
  def removeTransactionById: ServerEndpoint[Any, IO]
  def createTransaction: ServerEndpoint[Any, IO]
}

object CashTransactionController{
  final private class Impl(storage: CashTransactionStorage) extends CashTransactionController {

    override val listAllTransactions: ServerEndpoint[Any, IO] =
      listTransactions.serverLogic{ctx =>
        storage.list.map(_.leftMap[AppError](identity))
      }

    override val findTransactionById: ServerEndpoint[Any, IO] =
      Endpoints.findTransactionById.serverLogic { case (ctx, id) =>
        storage.findById(id).map(_.leftMap[AppError](identity))
      }

    override val removeTransactionById: ServerEndpoint[Any, IO] =
      Endpoints.removeTransactionById.serverLogic { case (ctx, id) =>
        storage.removeById(id)
      }

    override val createTransaction: ServerEndpoint[Any, IO] =
      Endpoints.createTransaction.serverLogic { case (ctx, tran) =>
        storage.create(tran).attempt.flatMap {
          case Right(cashTransaction) => IO.pure(cashTransaction.asRight[AppError])
          case Left(error) => IO.pure(CreatingError().asLeft[CashTransaction])
        }
      }
  }

  def make(storage: CashTransactionStorage ):CashTransactionController = new Impl(storage)
}