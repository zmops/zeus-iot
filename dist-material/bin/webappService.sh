#!/usr/bin/env bash
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
[ -z "$WEBAPP_HOME" ] && WEBAPP_HOME=$(cd "$PRGDIR/.." > /dev/null || exit 1; pwd)

WEBAPP_LOG_DIR="${WEBAPP_LOG_DIR:-${WEBAPP_HOME}/logs}"
JAVA_OPTS="${JAVA_OPTS:-  -Xms512M -Xmx1024M}"
JAR_PATH="${WEBAPP_HOME}/webapp"

if [ ! -d "${WEBAPP_LOG_DIR}" ]; then
    mkdir -p "${WEBAPP_LOG_DIR}"
fi

LOG_FILE_LOCATION=${WEBAPP_LOG_DIR}/webapp.log

_RUNJAVA=${JAVA_HOME}/bin/java
[ -z "$JAVA_HOME" ] && _RUNJAVA=java

eval exec "\"$_RUNJAVA\" ${JAVA_OPTS} -jar ${JAR_PATH}/zeus-webapp.jar \
         --spring.config.location=${JAR_PATH}/webapp.yml \
         --logging.file=${LOG_FILE_LOCATION} \
        &> ${WEBAPP_LOG_DIR}/webapp-console.log &"

if [ $? -eq 0 ]; then
    sleep 1
	echo "Zeus Web Application started successfully!"
else
	echo "Zeus Web Application started failure!"
	exit 1
fi
