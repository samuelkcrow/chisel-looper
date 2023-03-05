#!/bin/bash
if [ $# -eq 2 ]
  then
    play -r 8k -e signed -b 16 -c 1 $1 repeat $2
  else
    play -r 8k -e signed -b 16 -c 1 $1
fi
