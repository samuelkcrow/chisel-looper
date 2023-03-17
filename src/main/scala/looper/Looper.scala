// See README.md for license details.

package looper

import chisel3._
import chisel3.util._

case class LooperParams(numSamples: Int, bytesPerSample: Int, maxLoops: Int) {
  require(numSamples > 0)
  require(bytesPerSample > 0)
  require(maxLoops > 0)
}

class Looper(p: LooperParams) extends Module {
  private val bitWidth = p.bytesPerSample * 8
  private val sample = SInt(bitWidth.W)
  private val intMin = -(1 << (bitWidth - 1)).S
  private val intMax = ((1 << (bitWidth - 1)) - 1).S

  val io = IO(new Bundle {
    val loadLoop  = Input(Bool())
    val playLoop  = Input(Bool())
    val loadAddr  = Input(UInt(log2Ceil(p.maxLoops).W))
    val sampleIn  = Input(sample)
    val sampleOut = Output(sample)
  })

  io.sampleOut := 0.S


  val loopMems = Mem(p.maxLoops * p.numSamples, sample)
  val loadCounter = Counter(p.numSamples)
  val playCounter = Counter(p.numSamples)
  val loadIndex = loadCounter.value
  val playIndex = playCounter.value

  val memAddrWidth = log2Ceil(p.maxLoops * p.numSamples)
  val samplesVec = Wire(Vec(p.maxLoops, SInt()))
  for (addr <- 0 until p.maxLoops) {
    samplesVec(addr) := loopMems(addr.U * p.numSamples.U(memAddrWidth.W) + playIndex)
  }
  val sumOfSamples = samplesVec reduce {_ +& _}

  when(io.loadLoop) {
    loopMems(io.loadAddr * p.numSamples.U(memAddrWidth.W) + loadIndex) := io.sampleIn
    loadCounter.inc()
  }

  when(io.playLoop) {
    // saturating addition (could use experimental interval type with .clip instead)
    when(sumOfSamples > intMax) { io.sampleOut := intMax }
      .elsewhen(sumOfSamples < intMin) { io.sampleOut := intMin }
      .otherwise { io.sampleOut := sumOfSamples }
    playCounter.inc()
  }

}
