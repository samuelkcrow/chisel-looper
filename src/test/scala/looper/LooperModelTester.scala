package looper

import scala.collection.mutable.ArrayBuffer

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class LooperModelTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "LooperModel"

  it should "do something" in {
    val p = LooperParams(128, 4, 1)
    val m = LooperModel(p)
    assert(true)
  }
}
