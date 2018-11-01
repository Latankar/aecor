package aecor.runtime.queue

import aecor.macros.boopickleWireProtocol
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import boopickle.Default._
import cats.tagless.autoFunctorK

@boopickleWireProtocol
@autoFunctorK
trait Counter[F[_]] {
  def increment: F[Long]
  def decrement: F[Long]
  def value: F[Long]
}

object Counter {
  def inmem[F[_]: Sync]: F[Counter[F]] =
    Ref[F].of(0L).map { ref =>
      new Counter[F] {
        override def increment: F[Long] = ref.update(_ + 1L) >> value
        override def decrement: F[Long] = ref.update(_ - 1L) >> value
        override def value: F[Long] = ref.get
      }
    }
}

final case class CounterId(value: String) extends AnyVal