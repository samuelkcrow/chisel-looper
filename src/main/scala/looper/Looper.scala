// See README.md for license details.

package looper

import chisel3._
import chisel3.util._

case class LooperParams(numSamples: Int, bytesPerSample: Int, maxLoops: Int) {
  require(numSamples > 0) // the number of samples in a single loop
  require(bytesPerSample > 0) // bytes per sample (i.e 1 byte per sample == 8 bit samples)
  require(maxLoops > 0) // the number of loops that can be stored in memory
}

class Looper(p: LooperParams) extends Module {
  private val bitWidth = p.bytesPerSample * 8
  private val sample = SInt(bitWidth.W) // SInts are the most common raw audio format
  // These min and max values are needed for saturating addition
  private val intMin = -(1 << (bitWidth - 1)).S
  private val intMax = ((1 << (bitWidth - 1)) - 1).S

  val io = IO(new Bundle {
    val loadLoop  = Input(Bool()) // assert to load a loop in, one sample per cycle
    val playLoop  = Input(Bool()) // play the stored (combined) loops, one sample per cycle
    val loadAddr  = Input(UInt(log2Ceil(p.maxLoops).W)) // the address of the loop to load into
    val sampleIn  = Input(sample)
    val sampleOut = Output(sample)
  })

  // default output when not playing back
  io.sampleOut := 0.S

  val loopMems = Mem(p.maxLoops * p.numSamples, sample) // all samples stored in contiguous memory
  val loadCounter = Counter(p.numSamples) // keeps track of where to store next sample to
  val playCounter = Counter(p.numSamples) // keeps track of where to play next sample from
  val loadIndex = loadCounter.value
  val playIndex = playCounter.value

  val memAddrWidth = log2Ceil(p.maxLoops * p.numSamples) // required to avoid bad width inference in following mem access
  val samplesVec = Wire(Vec(p.maxLoops, SInt())) // using a vec for this allows use of reduce below
  for (addr <- 0 until p.maxLoops) {
    samplesVec(addr) := loopMems(addr.U * p.numSamples.U(memAddrWidth.W) + playIndex)
  }
  val sumOfSamples = samplesVec reduce {_ +& _} // expanding width addition required for saturating addition

  // loading and playing at the same time is possible but not defined
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
