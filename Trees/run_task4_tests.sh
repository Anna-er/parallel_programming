#!/bin/bash

REPEAT_COUNT=10

for i in $(seq 1 $REPEAT_COUNT)
do
  echo "Run #$i"
  ./gradlew test
  if [ $? -ne 0 ]; then
    echo "Test run #$i failed."
    exit 1
  fi
done
