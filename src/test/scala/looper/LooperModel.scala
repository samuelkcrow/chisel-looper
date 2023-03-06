package looper

import scala.collection.mutable.ArrayBuffer

class LooperModel(p: LooperParams) {
  // mutable state variables
  private var playIndex: Int = p.numSamples - 1 // these start at end of their range
  private var loadIndex: Int = p.numSamples - 1 // because they increment before use

  private val loopMem: ArrayBuffer[String] = ArrayBuffer.fill(p.numSamples)("00")

  // incrementing before using the indices makes the output behavior easier
  // it's not really necessary when loading, but more consistent this way
  def inputSample(sampleValue: String): Unit = {
    loadIndex = (loadIndex + 1) % p.numSamples
    loopMem(loadIndex) = sampleValue
  }

  def outputSample(): String = {
    playIndex = (playIndex + 1) % p.numSamples
    loopMem(playIndex)
  }
}