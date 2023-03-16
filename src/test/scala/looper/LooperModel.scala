package looper

import scala.collection.mutable.ArrayBuffer

class LooperModel(p: LooperParams) {
  // mutable state variables
  private var playIndex: Int = p.numSamples - 1 // these start at end of their range
  private var loadIndex: Int = p.numSamples - 1 // because they increment before use

  private val loopMem: ArrayBuffer[Int] = ArrayBuffer.fill(p.numSamples)(0)

  // incrementing before using the indices makes the output behavior easier
  // it's not really necessary when loading, but more consistent this way
  def inputSample(sampleValue: Int): Unit = {
    loadIndex = (loadIndex + 1) % p.numSamples
    loopMem(loadIndex) = sampleValue
  }

  def outputSample(): Int = {
    playIndex = (playIndex + 1) % p.numSamples
    loopMem(playIndex)
  }
}