#!/bin/sh

# Computes the absolute path of eXo
cd `dirname "$0"`

# Sets some variables
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger"
SECURITY_OPTS="-Djava.security.auth.login.config=../conf/jaas.conf"
EXO_CONFIG_OPTS="-Xshare:auto -Xms128m -Xmx512m"

#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_CONFIG_OPTS $REMOTE_DEBUG"
export JAVA_OPTS

# Launches the server
exec "$PRGDIR"./catalina.sh "$@"
