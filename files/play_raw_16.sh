#!/bin/bash
if [ $# -eq 2 ]
  then
    play -r 16k -e signed -b 8 -c 1 $1 repeat $2
  else
    play -r 16k -e signed -b 8 -c 1 $1
fi
