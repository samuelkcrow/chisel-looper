package looper

import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.mutable.ArrayBuffer


class LooperModelTester extends AnyFlatSpec {
  behavior of "LooperModel"

  val f = new FileIO
  val audio_350Hz = f.readAudio("files/8bit8khzRaw/350Hz_-3dBFS_1s.raw")
  val audio_440Hz = f.readAudio("files/8bit8khzRaw/440Hz_-3dBFS_1s.raw")

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
}
