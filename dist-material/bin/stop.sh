#!/bin/bash
ps -ef | grep zeus-iot-bin | grep -v grep | awk '{print $2}' | xargs kill -9
