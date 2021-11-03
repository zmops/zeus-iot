#!/bin/bash

set -o pipefail

set +e
set -x
# Script trace mode
if [ "${DEBUG_MODE,,}" == "true" ]; then
    set -o xtrace
fi

#Enable PostgreSQL timescaleDB feature:
: ${ENABLE_TIMESCALEDB:="false"}

# Default directories
# User 'zabbix' home directory
ZABBIX_USER_HOME_DIR="/var/lib/zabbix"
# Configuration files directory
ZABBIX_ETC_DIR="/etc/zabbix"

# usage: file_env VAR [DEFAULT]
# as example: file_env 'MYSQL_PASSWORD' 'zabbix'
#    (will allow for "$MYSQL_PASSWORD_FILE" to fill in the value of "$MYSQL_PASSWORD" from a file)
# unsets the VAR_FILE afterwards and just leaving VAR
file_env() {
    local var="$1"
    local fileVar="${var}_FILE"
    local defaultValue="${2:-}"

    if [ "${!var:-}" ] && [ "${!fileVar:-}" ]; then
        echo "**** Both variables $var and $fileVar are set (but are exclusive)"
        exit 1
    fi

    local val="$defaultValue"

    if [ "${!var:-}" ]; then
        val="${!var}"
        echo "** Using ${var} variable from ENV"
    elif [ "${!fileVar:-}" ]; then
        if [ ! -f "${!fileVar}" ]; then
            echo "**** Secret file \"${!fileVar}\" is not found"
            exit 1
        fi
        val="$(< "${!fileVar}")"
        echo "** Using ${var} variable from secret file"
    fi
    export "$var"="$val"
    unset "$fileVar"
}

escape_spec_char() {
    local var_value=$1

    var_value="${var_value//\\/\\\\}"
    var_value="${var_value//[$'\n']/}"
    var_value="${var_value//\//\\/}"
    var_value="${var_value//./\\.}"
    var_value="${var_value//\*/\\*}"
    var_value="${var_value//^/\\^}"
    var_value="${var_value//\$/\\\$}"
    var_value="${var_value//\&/\\\&}"
    var_value="${var_value//\[/\\[}"
    var_value="${var_value//\]/\\]}"

    echo "$var_value"
}

update_config_var() {
    local config_path=$1
    local var_name=$2
    local var_value=$3
    local is_multiple=$4

    local masklist=("DBPassword TLSPSKIdentity")

    if [ ! -f "$config_path" ]; then
        echo "**** Configuration file '$config_path' does not exist"
        return
    fi

    if [[ " ${masklist[@]} " =~ " $var_name " ]] && [ ! -z "$var_value" ]; then
        echo -n "** Updating '$config_path' parameter \"$var_name\": '****'. Enable DEBUG_MODE to view value ..."
    else
        echo -n "** Updating '$config_path' parameter \"$var_name\": '$var_value'..."
    fi

    # Remove configuration parameter definition in case of unset parameter value
    if [ -z "$var_value" ]; then
        sed -i -e "/^$var_name=/d" "$config_path"
        echo "removed"
        return
    fi

    # Remove value from configuration parameter in case of double quoted parameter value
    if [ "$var_value" == '""' ]; then
        sed -i -e "/^$var_name=/s/=.*/=/" "$config_path"
        echo "undefined"
        return
    fi

    # Use full path to a file for TLS related configuration parameters
    if [[ $var_name =~ ^TLS.*File$ ]]; then
        var_value=$ZABBIX_USER_HOME_DIR/enc/$var_value
    fi

    # Escaping characters in parameter value and name
    var_value=$(escape_spec_char "$var_value")
    var_name=$(escape_spec_char "$var_name")

    if [ "$(grep -E "^$var_name=" $config_path)" ] && [ "$is_multiple" != "true" ]; then
        sed -i -e "/^$var_name=/s/=.*/=$var_value/" "$config_path"
        echo "updated"
    elif [ "$(grep -Ec "^# $var_name=" $config_path)" -gt 1 ]; then
        sed -i -e  "/^[#;] $var_name=$/i\\$var_name=$var_value" "$config_path"
        echo "added first occurrence"
    else
        sed -i -e "/^[#;] $var_name=/s/.*/&\n$var_name=$var_value/" "$config_path"
        echo "added"
    fi

}

update_config_multiple_var() {
    local config_path=$1
    local var_name=$2
    local var_value=$3

    var_value="${var_value%\"}"
    var_value="${var_value#\"}"

    local IFS=,
    local OPT_LIST=($var_value)

    for value in "${OPT_LIST[@]}"; do
        update_config_var $config_path $var_name $value true
    done
}

# Check prerequisites for PostgreSQL database
check_variables_postgresql() {
    file_env POSTGRES_USER
    file_env POSTGRES_PASSWORD

    : ${DB_SERVER_HOST:="postgres-server"}
    : ${DB_SERVER_PORT:="5432"}

    DB_SERVER_ROOT_USER=${POSTGRES_USER:-"postgres"}
    DB_SERVER_ROOT_PASS=${POSTGRES_PASSWORD:-""}

    DB_SERVER_ZEUS_USER=${POSTGRES_USER:-"postgres"}
    DB_SERVER_ZEUS_PASS=${POSTGRES_PASSWORD:-"postgres"}

    : ${DB_SERVER_SCHEMA:="public"}

    DB_SERVER_DBNAME=${POSTGRES_DB:-"zeus-iot"}

    : ${POSTGRES_USE_IMPLICIT_SEARCH_PATH:="false"}
}

check_db_connect_postgresql() {
    echo "********************"
    echo "* DB_SERVER_HOST: ${DB_SERVER_HOST}"
    echo "* DB_SERVER_PORT: ${DB_SERVER_PORT}"
    echo "* DB_SERVER_DBNAME: ${DB_SERVER_DBNAME}"
    echo "* DB_SERVER_SCHEMA: ${DB_SERVER_SCHEMA}"
    if [ "${DEBUG_MODE,,}" == "true" ]; then
        echo "* DB_SERVER_ZEUS_USER: ${DB_SERVER_ZEUS_USER}"
        echo "* DB_SERVER_ZEUS_PASS: ${DB_SERVER_ZEUS_PASS}"
    fi
    echo "********************"

    if [ -n "${DB_SERVER_ZEUS_PASS}" ]; then
        export PGPASSWORD="${DB_SERVER_ZEUS_PASS}"
    fi

    WAIT_TIMEOUT=5

    if [ "${POSTGRES_USE_IMPLICIT_SEARCH_PATH,,}" == "false" ] && [ -n "${DB_SERVER_SCHEMA}" ]; then
        PGOPTIONS="--search_path=${DB_SERVER_SCHEMA}"
        export PGOPTIONS
    fi

    if [ -n "${ZEUS_DBTLSCONNECT}" ]; then
        export PGSSLMODE=${ZEUS_DBTLSCONNECT//_/-}
        export PGSSLROOTCERT=${ZEUS_DBTLSCAFILE}
        export PGSSLCERT=${ZEUS_DBTLSCERTFILE}
        export PGSSLKEY=${ZEUS_DBTLSKEYFILE}
    fi

    while true :
    do
        psql --host ${DB_SERVER_HOST} --port ${DB_SERVER_PORT} --username ${DB_SERVER_ROOT_USER} --list --quiet 1>/dev/null 2>&1 && break
        psql --host ${DB_SERVER_HOST} --port ${DB_SERVER_PORT} --username ${DB_SERVER_ROOT_USER} --list --dbname ${DB_SERVER_DBNAME} --quiet 1>/dev/null 2>&1 && break

        echo "**** PostgreSQL server is not available. Waiting $WAIT_TIMEOUT seconds..."
        sleep $WAIT_TIMEOUT
    done

    unset PGPASSWORD
    unset PGOPTIONS
    unset PGSSLMODE
    unset PGSSLROOTCERT
    unset PGSSLCERT
    unset PGSSLKEY
}

psql_query() {
    query=$1
    db=$2

    local result=""

    if [ -n "${DB_SERVER_ZBX_PASS}" ]; then
        export PGPASSWORD="${DB_SERVER_ZBX_PASS}"
    fi

    if [ "${POSTGRES_USE_IMPLICIT_SEARCH_PATH,,}" == "false" ] && [ -n "${DB_SERVER_SCHEMA}" ]; then
        PGOPTIONS="--search_path=${DB_SERVER_SCHEMA}"
        export PGOPTIONS
    fi

    if [ -n "${ZBX_DBTLSCONNECT}" ]; then
        export PGSSLMODE=${ZBX_DBTLSCONNECT//_/-}
        export PGSSLROOTCERT=${ZBX_DBTLSCAFILE}
        export PGSSLCERT=${ZBX_DBTLSCERTFILE}
        export PGSSLKEY=${ZBX_DBTLSKEYFILE}
    fi

    result=$(psql --no-align --quiet --tuples-only --host "${DB_SERVER_HOST}" --port "${DB_SERVER_PORT}" \
             --username "${DB_SERVER_ROOT_USER}" --command "$query" --dbname "$db" 2>/dev/null);

    unset PGPASSWORD
    unset PGOPTIONS
    unset PGSSLMODE
    unset PGSSLROOTCERT
    unset PGSSLCERT
    unset PGSSLKEY

    echo $result
}

create_db_database_postgresql() {
    DB_EXISTS=$(psql_query "SELECT 1 AS result FROM pg_database WHERE datname='${DB_SERVER_DBNAME}'" "${DB_SERVER_DBNAME}")

    if [ -z ${DB_EXISTS} ]; then
        echo "** Database '${DB_SERVER_DBNAME}' does not exist. Creating..."

        if [ -n "${DB_SERVER_ZEUS_PASS}" ]; then
            export PGPASSWORD="${DB_SERVER_ZEUS_PASS}"
        fi

        if [ "${POSTGRES_USE_IMPLICIT_SEARCH_PATH,,}" == "false" ] && [ -n "${DB_SERVER_SCHEMA}" ]; then
            PGOPTIONS="--search_path=${DB_SERVER_SCHEMA}"
            export PGOPTIONS
        fi

        if [ -n "${ZBX_DBTLSCONNECT}" ]; then
            export PGSSLMODE=${ZBX_DBTLSCONNECT//_/-}
            export PGSSLROOTCERT=${ZBX_DBTLSCAFILE}
            export PGSSLCERT=${ZBX_DBTLSCERTFILE}
            export PGSSLKEY=${ZBX_DBTLSKEYFILE}
        fi

        createdb --host "${DB_SERVER_HOST}" --port "${DB_SERVER_PORT}" --username "${DB_SERVER_ROOT_USER}" \
                 --owner "${DB_SERVER_ZEUS_USER}" "${DB_SERVER_DBNAME}"

        unset PGPASSWORD
        unset PGOPTIONS
        unset PGSSLMODE
        unset PGSSLROOTCERT
        unset PGSSLCERT
        unset PGSSLKEY
    else
        echo "** Database '${DB_SERVER_DBNAME}' already exists. Please be careful with database owner!"
    fi

    psql_query "CREATE SCHEMA IF NOT EXISTS ${DB_SERVER_SCHEMA}" "${DB_SERVER_DBNAME}" 1>/dev/null
}

create_db_schema_postgresql() {
    DBVERSION_TABLE_EXISTS=$(psql_query "SELECT 1 FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = 
                                         c.relnamespace WHERE  n.nspname = '$DB_SERVER_SCHEMA' AND c.relname = 'dbversion'" "${DB_SERVER_DBNAME}")

    if [ -n "${DBVERSION_TABLE_EXISTS}" ]; then
        echo "** Table '${DB_SERVER_DBNAME}.dbversion' already exists."
        ZEUS_DB_VERSION=$(psql_query "SELECT mandatory FROM ${DB_SERVER_SCHEMA}.dbversion" "${DB_SERVER_DBNAME}")
    fi

    if [ -z "${ZEUS_DB_VERSION}" ]; then
        echo "** Creating '${DB_SERVER_DBNAME}' schema in PostgreSQL"

        if [ "${ENABLE_TIMESCALEDB,,}" == "true" ]; then
            psql_query "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;" "${DB_SERVER_DBNAME}"
        fi

        if [ -n "${DB_SERVER_ZEUS_PASS}" ]; then
            export PGPASSWORD="${DB_SERVER_ZBX_PASS}"
        fi

        if [ "${POSTGRES_USE_IMPLICIT_SEARCH_PATH,,}" == "false" ] && [ -n "${DB_SERVER_SCHEMA}" ]; then
            PGOPTIONS="--search_path=${DB_SERVER_SCHEMA}"
            export PGOPTIONS
        fi

        if [ -n "${ZBX_DBTLSCONNECT}" ]; then
            export PGSSLMODE=${ZBX_DBTLSCONNECT//_/-}
            export PGSSLROOTCERT=${ZBX_DBTLSCAFILE}
            export PGSSLCERT=${ZBX_DBTLSCERTFILE}
            export PGSSLKEY=${ZBX_DBTLSKEYFILE}
        fi

        cat /zeus-iot-bin/bin/sql/zeus-iot.sql | psql --quiet \
                --host "${DB_SERVER_HOST}" --port "${DB_SERVER_PORT}" \
                --username "${DB_SERVER_ZEUS_USER}" --dbname "${DB_SERVER_DBNAME}" 1>/dev/null || exit 1

        unset PGPASSWORD
        unset PGOPTIONS
        unset PGSSLMODE
        unset PGSSLROOTCERT
        unset PGSSLCERT
        unset PGSSLKEY
    fi
}


prepare_server() {
    echo "** Preparing Zeus IoT Server"

    check_variables_postgresql
    check_db_connect_postgresql
    create_db_database_postgresql
    create_db_schema_postgresql

}

#################################################
startiot() {

    [ -z "$IOT_HOME" ] && IOT_HOME=/zeus-iot-bin
    JAVA_OPTS="${JAVA_OPTS:-  -Xms256M -Xmx512M}"
    _RUNJAVA=${JAVA_HOME}/bin/java
    [ -z "$JAVA_HOME" ] && _RUNJAVA=java

    CLASSPATH="$IOT_HOME/config"
    for i in "$IOT_HOME"/iot-server-libs/*.jar
    do
        CLASSPATH="$i:$CLASSPATH"
    done

    IOT_OPTIONS=" -Duser.timezone=GMT+08"

    eval exec "\"$_RUNJAVA\" ${JAVA_OPTS} ${IOT_OPTIONS} -classpath $CLASSPATH com.zmops.zeus.iot.server.starter.IoTServerStartUp &> /dev/stdout &"
}

startwebapp() {

    [ -z "$WEBAPP_HOME" ] && WEBAPP_HOME=/zeus-iot-bin

    JAVA_OPTS="${JAVA_OPTS:-  -Xms512M -Xmx1024M}"
    JAR_PATH="${WEBAPP_HOME}/webapp"

    _RUNJAVA=${JAVA_HOME}/bin/java
    [ -z "$JAVA_HOME" ] && _RUNJAVA=java

    sed -i "s%\(zbxApiToken: \).*%\1${ZBXAPITOKEN}%g" ${JAR_PATH}/webapp.yml	

    eval exec "\"$_RUNJAVA\" ${JAVA_OPTS} -jar ${JAR_PATH}/zeus-webapp.jar \
             --spring.config.location=${JAR_PATH}/webapp.yml"

}


prepare_server
startiot
startwebapp

#################################################
