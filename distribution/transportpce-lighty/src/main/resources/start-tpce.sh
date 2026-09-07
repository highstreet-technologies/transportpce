#!/bin/bash
###
# ============LICENSE_START=======================================================
# TransportPCE :: Lighty Distribution
# Copyright © 2026 Highstreet Technologies GmbH. All rights reserved.
# Licensed under the Apache License, Version 2.0 (the "License");
# ============LICENSE_END=========================================================
###

set -e

cd "${TPCE_HOME}"

# JVM options — can be overridden via environment
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx2G}"

# Add opens required by Lighty/ODL on Java 17+
JDK_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED"

echo "Starting TransportPCE on Lighty.io..."
echo "  TPCE_HOME=${TPCE_HOME}"
echo "  JAVA_OPTS=${JAVA_OPTS}"
echo "  JDK_OPTS=${JDK_OPTS}"

exec java ${JAVA_OPTS} ${JDK_OPTS} -jar tpce.jar "$@"
