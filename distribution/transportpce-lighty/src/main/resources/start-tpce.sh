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

# Datastore timeouts — Lighty needs longer timeouts than Karaf during startup
# because MDSAL/Akka cluster initialization is asynchronous
export TRANSPORTPCE_TIMEOUT_DATASTORE_READ="${TRANSPORTPCE_TIMEOUT_DATASTORE_READ:-5000}"
export TRANSPORTPCE_TIMEOUT_DATASTORE_WRITE="${TRANSPORTPCE_TIMEOUT_DATASTORE_WRITE:-5000}"
export TRANSPORTPCE_TIMEOUT_DATASTORE_DELETE="${TRANSPORTPCE_TIMEOUT_DATASTORE_DELETE:-5000}"

# Device port-state poll budget after posting an interface. Simulated networks
# (pynts/NTS) never reflect the interface on the port, so every interface burns
# the full budget — keep it short for simulators, raise it for real devices.
export TRANSPORTPCE_TIMEOUT_DEVICE_PORT_UPDATE="${TRANSPORTPCE_TIMEOUT_DEVICE_PORT_UPDATE:-30000}"

echo "Starting TransportPCE on Lighty.io..."
echo "  TPCE_HOME=${TPCE_HOME}"
echo "  JAVA_OPTS=${JAVA_OPTS}"
echo "  JDK_OPTS=${JDK_OPTS}"

exec java ${JAVA_OPTS} ${JDK_OPTS} -jar tpce.jar -restconf config.json "$@"
