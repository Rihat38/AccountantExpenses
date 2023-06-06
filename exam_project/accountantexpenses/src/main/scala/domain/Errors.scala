package domain

import cats.syntax.option._
import domain.Fields.TranId
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import sttp.tapir.Schema

object Errors {
  sealed abstract class AppError(
      val message: String,
      val cause: Option[Throwable] = None
  )
  object AppError{
    implicit val encoder: Encoder[AppError] = new Encoder[AppError] {
      override def apply(a: AppError): Json = Json.obj(
        ("message", Json.fromString(a.message))
      )
    }

    implicit val decoder: Decoder[AppError] = new Decoder[AppError] {
      override def apply(c: HCursor): Result[AppError] =
        c.downField("message").as[String].map(MockError)
    }

    implicit val schema: Schema[AppError] = Schema.string[AppError]
  }
  case class TransactionNotFound(id: TranId)
      extends AppError(s"Transaction with id=${id.value} not found")
  case class CreatingError()
      extends AppError("Error when trying to enter a transaction")
  case class InternalError(internalCause: Throwable)
      extends AppError("Internal error", internalCause.some)
  case class MockError(mockMessage: String) extends AppError(mockMessage)
}
