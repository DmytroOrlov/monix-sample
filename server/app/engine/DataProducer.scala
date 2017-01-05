package engine

import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import shared.models.Point
import util.Random
import scala.concurrent.duration._

final class DataProducer(interval: FiniteDuration, seed: Long)
  extends Observable[Point] {

  private case class State(x: Int, y: Int, ts: Long)

  override def unsafeSubscribeFn(subscriber: Subscriber[Point]): Cancelable = {
    import subscriber.{scheduler => s}

    val random = Observable
      .fromStateAction(Random.intInRange(-20, 20))(s.currentTimeMillis() + seed)
      .flatMap { x => Observable.now(x).delaySubscription(interval) }

    val generator = random.scan(Point(0, s.currentTimeMillis())) {
      case (Point(value, _), rnd) =>
        val next = value + rnd
        Point(next, s.currentTimeMillis())
    }

    generator
      .drop(1)
      .unsafeSubscribeFn(subscriber)
  }
}
