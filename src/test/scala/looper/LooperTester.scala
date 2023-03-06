// See README.md for license details.

package looper

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.mutable.ArrayBuffer


/**
  * Test the Chisel implementation of the looper
  */
class LooperTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Looper"
  it should "read in then out a single loop" in {
    val p = LooperParams(numSamples = 10, bytesPerSample = 2)
    val loop: ArrayBuffer[Int] = ArrayBuffer.fill(p.numSamples)(0)
    for (index <- 0 until p.numSamples) {
      loop(index) = index
    }
    test(new Looper(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.loadLoop.poke(true.B)
      for (index <- 0 until p.numSamples) {
        dut.io.sampleIn.poke(loop(index))
        dut.clock.step()
      }
      dut.io.loadLoop.poke(false.B)
      dut.io.playLoop.poke(true.B)
      for (index <- 0 until p.numSamples) {
        dut.io.sampleOut.expect(loop(index).S)
        dut.clock.step()
      }
    }
  }
}
