package controller

import sttp.tapir._
import sttp.tapir.json.circe._
import domain._
import domain.Errors._
import domain.Fields.TranId

object Endpoints {
  val listTransactions: PublicEndpoint[RequestContext, AppError, List[CashTransaction], Any] =
    endpoint
      .get
      .in("transactionsList")
      .in(header[String]("X-Request-Id"))
      .mapIn(RequestContext)(_.requestId)
      .errorOut(jsonBody[AppError])
      .out(jsonBody[List[CashTransaction]])

  val findTransactionById: PublicEndpoint[(RequestContext, TranId), AppError, Option[CashTransaction], Any] =
    endpoint
      .get
      .in(header[String]("X-Request-Id"))
      .mapIn(RequestContext)(_.requestId)
      .in("transaction" / path[TranId])
      .errorOut(jsonBody[AppError])
      .out(jsonBody[Option[CashTransaction]])

  val removeTransactionById: PublicEndpoint[(RequestContext, TranId), AppError, Unit, Any] =
    endpoint
      .delete
      .in(header[String]("X-Request-Id"))
      .mapIn(RequestContext)(_.requestId)
      .in("transaction" / path[TranId])
      .errorOut(jsonBody[AppError])

  val createTransaction: PublicEndpoint[(RequestContext, CreateCashTransaction), AppError, CashTransaction, Any] =
    endpoint
      .post
      .in("transaction")
      .in(header[String]("X-Request-Id"))
      .mapIn(RequestContext)(_.requestId)
      .in(jsonBody[CreateCashTransaction])
      .errorOut(jsonBody[AppError])
      .out(jsonBody[CashTransaction])
}
