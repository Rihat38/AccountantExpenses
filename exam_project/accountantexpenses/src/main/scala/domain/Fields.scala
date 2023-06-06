package domain

import cats.data.ReaderT
import cats.effect.IO
import derevo.derive
import doobie.Read
import io.circe.Decoder._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.estatico.newtype.macros.newtype
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, Schema}
import tofu.syntax.loggable

import java.time.Instant

object Fields {
  @newtype
  case class TranId(value: Long)
  object TranId {
    implicit val doobieRead: Read[TranId] = Read[Long].map(TranId(_))

    implicit val schema: Schema[TranId] =
      Schema.schemaForLong.map(l => Some(TranId(l)))(_.value)

    implicit val encoder: Encoder[TranId] = new Encoder[TranId] {
      override def apply(a: TranId): Json = Json.obj(
        ("transactionId", Json.fromString(a.value.toString))
      )
    }

    implicit val decoder: Decoder[TranId] = new Decoder[TranId] {
      override def apply(c: HCursor): Result[TranId] =
        for {
          value <- c.downField("transactionId").as[Long]
        } yield TranId(value)
    }

    implicit val codec: Codec[String, TranId, TextPlain] =
      Codec.long.map(TranId(_))(_.value)
  }

  @newtype
  case class TranAmount(value: Double)
  object TranAmount {
    implicit val doobieRead: Read[TranAmount] = Read[Double].map(TranAmount(_))

    implicit val schema: Schema[TranAmount] =
      Schema.schemaForDouble.map(a => Some(TranAmount(a)))(_.value)

    implicit val encoder: Encoder[TranAmount] = new Encoder[TranAmount] {
      override def apply(a: TranAmount): Json = Json.obj(
        ("transactionId", Json.fromString(a.value.toString))
      )
    }

    implicit val decoder: Decoder[TranAmount] = new Decoder[TranAmount] {
      override def apply(c: HCursor): Result[TranAmount] =
        for {
          value <- c.downField("transactionId").as[Double]
        } yield TranAmount(value)
    }
  }

  @newtype
  case class TranName(value: String)
  object TranName {
    implicit val doobieRead: Read[TranName] = Read[String].map(TranName(_))

    implicit val schema: Schema[TranName] =
      Schema.schemaForString.map(n => Some(TranName(n)))(_.value)

    implicit val encoder: Encoder[TranName] = new Encoder[TranName] {
      override def apply(a: TranName): Json = Json.obj(
        ("transactionId", Json.fromString(a.value.toString))
      )
    }

    implicit val decoder: Decoder[TranName] = new Decoder[TranName] {
      override def apply(c: HCursor): Result[TranName] =
        for {
          value <- c.downField("transactionId").as[String]
        } yield TranName(value)
    }
  }

  @newtype
  case class TranDate(value: Instant)
  object TranDate {
    implicit val doobieRead: Read[TranDate] =
      Read[Long].map(ts => TranDate(Instant.ofEpochMilli(ts)))

    implicit val schema: Schema[TranDate] = Schema.schemaForString.map(d =>
      Some(TranDate(Instant.parse(d)))
    )(_.value.toString)

    implicit val encoder: Encoder[TranDate] = new Encoder[TranDate] {
      override def apply(a: TranDate): Json = Json.obj(
        ("transactionId", Json.fromString(a.value.toString))
      )
    }

    implicit val decoder: Decoder[TranDate] = new Decoder[TranDate] {
      override def apply(c: HCursor): Result[TranDate] =
        for {
          value <- c.downField("transactionId").as[String]
        } yield TranDate(Instant.parse(value))
    }
  }

  @newtype
  case class TranSource(value: String)
  object TranSource {
    implicit val doobieRead: Read[TranSource] = Read[String].map(TranSource(_))

    implicit val schema: Schema[TranSource] =
      Schema.schemaForString.map(s => Some(TranSource(s)))(_.value)

    implicit val encoder: Encoder[TranSource] = new Encoder[TranSource] {
      override def apply(a: TranSource): Json = Json.obj(
        ("transactionId", Json.fromString(a.value.toString))
      )
    }

    implicit val decoder: Decoder[TranSource] = new Decoder[TranSource] {
      override def apply(c: HCursor): Result[TranSource] =
        for {
          value <- c.downField("transactionId").as[String]
        } yield TranSource(value)
    }
  }

  type CtxIO[A] = ReaderT[IO, RequestContext, A]
}
