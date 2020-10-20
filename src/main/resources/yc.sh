#!/bin/bash

readonly nil="nil"
readonly output_is_nil="Usage: ./yc.h [-o output.c] input.y"

function usage {
  param=$1

  test $nil = $param && echo $output_is_nil && exit
}

output="./a.out"

while getopts o: option
do 
  case $option in
    "o" ) output=$OPTARG ;;
  esac
done

input=$1
usage $input

java -jar *.jar $input
cc out.c -o $output
