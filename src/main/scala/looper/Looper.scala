// See README.md for license details.

package looper

import chisel3._
import chisel3.util._

case class LooperParams(numSamples: Int, bytesPerSample: Int) {
  require(numSamples > 0)
  require(bytesPerSample > 0)
}

/**
  * Looper can only record one loop and play it back right now
  */
class Looper(p: LooperParams) extends Module {
  private val bitWidth = p.bytesPerSample * 8
  private val sample = SInt(bitWidth.W)
  private val intMin = -(1 << (bitWidth - 1)).S
  private val intMax = ((1 << (bitWidth - 1)) - 1).S

  val io = IO(new Bundle {
    val loadLoop  = Input(Bool())
    val playLoop  = Input(Bool())
    val sampleIn  = Input(sample)
    val sampleOut = Output(sample)
  })

  io.sampleOut := 0.S

  val loopMem = Mem(p.numSamples, sample)
  val loadCounter = Counter(p.numSamples)
  val playCounter = Counter(p.numSamples)
  val loadIndex = loadCounter.value
  val playIndex = playCounter.value

  when(io.loadLoop) {
    // saturating addition (could use experimental interval type with .clip instead)
    val tempSum = loopMem(loadIndex) +& io.sampleIn
    when(tempSum > intMax) {loopMem(loadIndex) := intMax}
      .elsewhen(tempSum < intMin) {loopMem(loadIndex) := intMin}
      .otherwise(loopMem(loadIndex) := tempSum)
    loadCounter.inc()
  }

  when(io.playLoop) {
    io.sampleOut := loopMem(playIndex)
    playCounter.inc()
  }
}