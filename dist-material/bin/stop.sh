#!/usr/bin/env bash
set -x
ps -ef | grep zeus-iot-bin | grep -v grep > /tmp/test
status=`ps -ef | grep zeus-iot-bin | grep java | grep -v grep | awk '{print $2}' | wc -l`

if [ $status -ne 0 ]
then
	for i in `ps -ef | grep zeus-iot-bin | grep -v grep | awk '{print $2}'`
	do
		kill -9 $i
	done
fi
