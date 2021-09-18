#!/bin/bash
status=`ps -ef | grep zeus-iot-bin | grep -v grep | awk '{print $2}' | wc -l`

if [ $status -ne 0 ]
then
	for i in `ps -ef | grep zeus-iot-bin | grep -v grep | awk '{print $2}'`
	do
		kill $i
	done
fi
