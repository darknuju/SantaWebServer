#!/bin/sh

cd `dirname $0`
cd ..

CLASSPATH=build

export CLASSPATH

java com.was.santa.Main $@
