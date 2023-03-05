// See README.md for license details.

package looper

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.mutable.ArrayBuffer


/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly gcd.GcdDecoupledTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly gcd.GcdDecoupledTester'
  * }}}
  */
class LooperTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Looper"
  it should "do something" in {
    val p = LooperParams(32, 4, 4)
    test(new Looper(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.in.poke(0.U)
      dut.clock.step()

      dut.io.out.expect(block)
    }
  }
}
