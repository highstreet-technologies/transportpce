#!/bin/sh

. $(dirname $0)/dockercmd_profile.sh

set -x
${DOCKER_CMD} build --no-cache --tag=${DOCKER_IMAGE} .
#${DOCKER_CMD} build --tag=${DOCKER_IMAGE} .
