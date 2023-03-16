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

  val f = new FileIO
  val audio_350Hz = f.readAudio("files/8bit8khzRaw/350Hz_-3dBFS_1s.raw")
  val audio_440Hz = f.readAudio("files/8bit8khzRaw/440Hz_-3dBFS_1s.raw")

  it should "read in then out a single loop" in {
    val p = LooperParams(numSamples = 8000, bytesPerSample = 1)

    // my input files have a null termination byte so I have to add the same
    val outBuffer: ArrayBuffer[Byte] = ArrayBuffer.fill(p.numSamples + 1)(0)

    val loop: ArrayBuffer[Int] = ArrayBuffer.fill(p.numSamples)(0)
    for (index <- 0 until p.numSamples) {
      loop(index) = audio_350Hz(index).toInt
    }
    test(new Looper(p)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(20000)
      dut.io.loadLoop.poke(true.B)
      for (index <- 0 until p.numSamples) {
        dut.io.sampleIn.poke(loop(index))
        dut.clock.step()
      }
      dut.io.loadLoop.poke(false.B)
      dut.clock.step(20) // step in the idle state, sanity check
      dut.io.playLoop.poke(true.B)
      for (index <- 0 until p.numSamples) {
        dut.io.sampleOut.expect(loop(index).S)
        outBuffer(index) = dut.io.sampleOut.peekInt().toByte
        dut.clock.step()
      }
      f.writeAudio("files/out/chisel350.raw", outBuffer.toArray)
    }
  }
}
