package looper

import java.nio.file.{Files, Paths}

class FileIO {
  def readAudio(inFile: String): Array[Byte] = {
    val inPath = Paths.get(inFile)
    Files.readAllBytes(inPath)
  }

  def writeAudio(outFile: String, byteArray: Array[Byte]): Unit = {
    val outPath = Paths.get(outFile)
    Files.write(outPath, byteArray)
  }
}