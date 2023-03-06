package looper

import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.mutable.ArrayBuffer


class LooperModelTester extends AnyFlatSpec {
  behavior of "LooperModel"

  it should "read in then out a single loop" in {
    val p = LooperParams(numSamples = 10, bytesPerSample = 2)
    val m = new LooperModel(p)
    // using strings while figuring out file input
    val loop: ArrayBuffer[String] = ArrayBuffer.fill(p.numSamples)("00")
    for (index <- 0 until p.numSamples) {
      loop(index) = index.toString + index.toString
    }

    for (index <- 0 until p.numSamples) {
      m.inputSample(loop(index))
    }

    for (index <- 0 until p.numSamples) {
      assert(m.outputSample == index.toString + index.toString)
    }

  }
}
