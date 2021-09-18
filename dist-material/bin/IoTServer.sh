#!/usr/bin/env sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

PRG="$0"
PRGDIR=$(dirname "$PRG")
[ -z "$IOT_HOME" ] && IOT_HOME=$(cd "$PRGDIR/.." > /dev/null || exit 1; pwd)

IOT_LOG_DIR="${IOT_LOG_DIR:-${IOT_HOME}/logs}"
JAVA_OPTS="${JAVA_OPTS:-  -Xms256M -Xmx512M}"

if [ ! -d "${IOT_LOG_DIR}" ]; then
    mkdir -p "${IOT_LOG_DIR}"
fi

_RUNJAVA=${JAVA_HOME}/bin/java
[ -z "$JAVA_HOME" ] && _RUNJAVA=java

CLASSPATH="$IOT_HOME/config:$CLASSPATH"
for i in "$IOT_HOME"/iot-server-libs/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done

IOT_OPTIONS=" -Doap.logDir=${IOT_LOG_DIR}"

eval exec "\"$_RUNJAVA\" ${JAVA_OPTS} ${IOT_OPTIONS} -classpath $CLASSPATH com.zmops.zeus.iot.server.starter.IOTServerStartUp \
        &> ${IOT_LOG_DIR}/zeus_iot.log &"

if [ $? -eq 0 ]; then
    sleep 1
	echo "Zeus IOT started successfully!"
else
	echo "Zeus IOT OAP started failure!"
	exit 1
fi
