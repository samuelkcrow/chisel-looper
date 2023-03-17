#!/bin/bash
if [ $# -eq 3 ]
  then
    sox -m -r 8k -e signed -b 8 -c 1 -v 1 $1 -r 8k -e signed -b 8 -c 1 -v 1 $2 $3
  else
    echo "Usage: args 1 & 2 are input audio files, arg 3 is output audio file"
fi
