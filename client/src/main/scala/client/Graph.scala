package client

import monix.execution.Ack
import monix.execution.Ack.Continue
import monix.reactive.Observer
import shared.models.Point

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => obj, _}

final class Graph(elementId: String)
  extends Observer[(Point, Point, Point, Point)] {

  private var chart: js.Dynamic = null

  def initChart(signal: (Point, Point, Point, Point)) = {
    val (first, second, third, fourth) = signal
    val timestamp = Seq(first.timestamp, second.timestamp, third.timestamp, fourth.timestamp).max / 1000

    global.jQuery(s"#$elementId").epoch(obj(
      "type" -> "time.line",
      "data" -> js.Array(
        obj(
          "label" -> "Series 1",
          "axes" -> js.Array("left", "bottom", "right"),
          "values" -> js.Array(obj(
            "time" -> timestamp,
            "y" -> first.value.toInt
          ))
        ),
        obj(
          "label" -> "Series 2",
          "axes" -> js.Array("left", "bottom", "right"),
          "values" -> js.Array(obj(
            "time" -> timestamp,
            "y" -> second.value.toInt
          ))
        ),
        obj(
          "label" -> "Series 3",
          "axes" -> js.Array("left", "bottom", "right"),
          "values" -> js.Array(obj(
            "time" -> timestamp,
            "y" -> third.value.toInt
          ))
        ),
        obj(
          "label" -> "Series 4",
          "axes" -> js.Array("left", "bottom", "right"),
          "values" -> js.Array(obj(
            "time" -> timestamp,
            "y" -> fourth.value.toInt
          ))
        )
      )
    ))
  }
    
  private def serialize(signal: (Point, Point, Point, Point)) = {
    val (first, second, third, fourth) = signal
    val timestamp = Seq(first.timestamp, second.timestamp, third.timestamp, fourth.timestamp).max / 1000

    js.Array(
      obj(
        "time" -> timestamp,
        "y" -> first.value.toInt
      ),
      obj(
        "time" -> timestamp,
        "y" -> second.value.toInt
      ),
      obj(
        "time" -> timestamp,
        "y" -> third.value.toInt
      ),
      obj(
        "time" -> timestamp,
        "y" -> fourth.value.toInt
      ))
  }

  def onNext(signal: (Point, Point, Point, Point)): Future[Ack] = {
    if (chart == null) {
      chart = initChart(signal)
    }
    else {
      chart.push(serialize(signal))
    }

    Continue
  }

  def onComplete(): Unit = ()
  def onError(ex: Throwable): Unit = {
    System.err.println(s"ERROR: $ex")
  }
}
