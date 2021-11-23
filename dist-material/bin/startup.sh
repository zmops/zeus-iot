#!/usr/bin/env bash
source /etc/profile
PRG="$0"
PRGDIR=`dirname "$PRG"`
IOT_EXE=IoTServer.sh
WEBAPP_EXE=webappService.sh

"$PRGDIR"/"$IOT_EXE"

"$PRGDIR"/"$WEBAPP_EXE"
