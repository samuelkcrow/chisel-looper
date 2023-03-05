// See README.md for license details.

package looper

import chisel3._
import chisel3.util._

case class LooperParams(numSamples: Int, bitsPerSample: Int) {
  require(numSamples > 0)
  require(bitsPerSample % 8 == 0)
}

/**
  * Looper can only record one loop and play it back right now
  */
class Looper(p: LooperParams) extends Module {
  val io = IO(new Bundle {
    val loadLoop  = Input(Bool())
    val playLoop  = Input(Bool())
    val sampleIn  = Input(SInt(p.bitsPerSample.W))
    val sampleOut = Output(SInt(p.bitsPerSample.W))
  })



}
