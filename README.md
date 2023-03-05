Chisel Looper
=======================

A looper is an audio effect used for building up combined musical phrases, commonly known as 
[Live Looping](https://en.wikipedia.org/wiki/Live_looping). This project aims use CHISEL to 
recreate the live looping behavior often found in guitar effects pedals such as
[these ones](https://www.guitarcenter.com/Looper-Effects-Pedals.gc).

## Using this repository

### Dependencies

#### JDK 11

I recommend LTS release Java 11. You can install the JDK as recommended by your operating system, 
or use the prebuilt binaries from [AdoptOpenJDK](https://adoptopenjdk.net/).

#### SBT

SBT is the most common built tool in the Scala community. 
You can download it [here](https://www.scala-sbt.org/download.html).  

#### SoX (recommended)

[SoX](https://en.wikipedia.org/wiki/SoX) can be used to convert most audio formats to the raw (PCM) format
used for audio input files ([examples](./files)) by this program. It can also play these raw files via the
included [bash script](./files/play_raw.sh), and play other audio files. See the SoX man pages for details.

### How to get started

#### ¯\\\_(ツ)_/¯
