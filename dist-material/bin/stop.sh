#!/usr/bin/env bash
set -x

status=`ps -ef | grep zeus-iot-bin | grep java | grep -v grep | awk '{print $2}' | wc -l`

while [ $status -ne 0 ]
do
        for i in `ps -ef | grep zeus-iot-bin | grep java | grep -v grep | awk '{print $2}'`
        do
                kill -9 $i
        done
        status=`ps -ef | grep zeus-iot-bin | grep java | grep -v grep | awk '{print $2}' | wc -l`
done