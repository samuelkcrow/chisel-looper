package looper

import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.mutable.ArrayBuffer


class LooperModelTester extends AnyFlatSpec {
  behavior of "LooperModel"

  val f = new FileIO
  val audio_350Hz = f.readAudio("files/8bit8khzRaw/350Hz_-3dBFS_1s.raw")
  val audio_440Hz = f.readAudio("files/8bit8khzRaw/440Hz_-3dBFS_1s.raw")
  val audio_dtone = f.readAudio("files/8bit8khzRaw/dial_tone.raw")

  it should "read in then out a single loop" in {
    val p = LooperParams(numSamples = 8000, bytesPerSample = 1)
    val m = new LooperModel(p)

    // my input files have a null termination byte so I have to add the same
    val outBuffer: ArrayBuffer[Byte] = ArrayBuffer.fill(p.numSamples + 1)(0)

    val loop: ArrayBuffer[Int] = ArrayBuffer.fill(p.numSamples)(0)
    for (index <- 0 until p.numSamples) {
      loop(index) = audio_350Hz(index).toInt
    }

    for (index <- 0 until p.numSamples) {
      m.inputSample(loop(index))
    }

    for (index <- 0 until p.numSamples) {
      val sampleOut = m.outputSample()
      assert(sampleOut == loop(index))
      outBuffer(index) = sampleOut.toByte
    }

    f.writeAudio("files/out/scala350.raw", outBuffer.toArray)
  }

  it should "read in two loops and play out the combination" in {
    val p = LooperParams(numSamples = 8000, bytesPerSample = 1)
    val m = new LooperModel(p)

    val outBuffer: ArrayBuffer[Byte] = ArrayBuffer.fill(p.numSamples + 1)(0)

    val sumLoop: ArrayBuffer[Int] = ArrayBuffer.fill(p.numSamples)(0)
    val loop: ArrayBuffer[Int] = ArrayBuffer.fill(p.numSamples)(0)
    // read in the first loop
    for (index <- 0 until p.numSamples) {
      loop(index) = audio_350Hz(index).toInt
      sumLoop(index) += loop(index)
    }
    for (index <- 0 until p.numSamples) {
      m.inputSample(loop(index))
    }

    // read in the second loop
    for (index <- 0 until p.numSamples) {
      loop(index) = audio_440Hz(index).toInt
      sumLoop(index) += loop(index)
      sumLoop(index) = sumLoop(index).min(127).max(-128) // saturating addition
    }
    for (index <- 0 until p.numSamples) {
      m.inputSample(loop(index))
    }

    for (index <- 0 until p.numSamples) {
      val sampleOut = m.outputSample()
      assert(sampleOut == audio_dtone(index).toInt) // confirm against known good audio
      outBuffer(index) = sampleOut.toByte
    }

//    f.writeAudio("files/out/scalaDT.raw", outBuffer.toArray)
  }
}
