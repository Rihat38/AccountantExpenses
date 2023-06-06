package domain

import domain.Fields._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.Schema

case class CreateCashTransaction(
    amount: TranAmount,
    name: TranName,
    date: TranDate,
    source: TranSource
)
object CreateCashTransaction{
  implicit val schema: Schema[CreateCashTransaction] = Schema.derived
  implicit val decoder: Decoder[CreateCashTransaction] = deriveDecoder[CreateCashTransaction]
  implicit val encoder: Encoder[CreateCashTransaction] = deriveEncoder[CreateCashTransaction]
}

case class CashTransaction(
    id: TranId,
    amount: TranAmount,
    name: TranName,
    date: TranDate,
    source: TranSource
)
object CashTransaction{
  implicit val schema: Schema[CashTransaction] = Schema.derived
  implicit val decoder: Decoder[CashTransaction] = deriveDecoder[CashTransaction]
  implicit val encoder: Encoder[CashTransaction] = deriveEncoder[CashTransaction]
}
