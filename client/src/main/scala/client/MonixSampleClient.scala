package client

import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import shared.models.Point

import scala.concurrent.duration._
import scala.scalajs.js

object MonixSampleClient extends js.JSApp {
  def main(): Unit = {
    val line1 = new DataConsumer(200.millis, 1274028492832L, doBackPressure = true)
      .collect { case s: Point => s }
    val line2 = new DataConsumer(200.millis, 9384729038472L, doBackPressure = true)
      .collect { case s: Point => s }
    val line3 = new DataConsumer(200.millis, -2938472934842L, doBackPressure = false)
      .collect { case s: Point => s }
    val line4 = new DataConsumer(200.millis, -9826395057397L, doBackPressure = false)
      .collect { case s: Point => s }

    Observable
      .combineLatest4(line1, line2, line3, line4)
      .subscribe(new Graph("lineChart"))
  }
}
