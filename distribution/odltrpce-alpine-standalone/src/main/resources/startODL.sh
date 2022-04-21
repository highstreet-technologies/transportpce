#!/bin/bash
###
# ============LICENSE_START=======================================================
# SDN-R
# ================================================================================
# Copyright (C) 2020 highstreet technologies GmbH. All rights reserved.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END=========================================================
###

function enable_odl_cluster(){
  if [ -z $SDNC_REPLICAS ]; then
     echo "SDNC_REPLICAS is not configured in Env field"
     exit
  fi

  echo "Installing Opendaylight cluster features"
  mv $ODL_HOME/etc/org.apache.karaf.features.cfg $ODL_HOME/etc/org.apache.karaf.features.cfg.orig
  cat $ODL_HOME/etc/org.apache.karaf.features.cfg.orig | sed -e "\|featuresBoot *=|s|$|,odl-mdsal-clustering,odl-jolokia|" > $ODL_HOME/etc/org.apache.karaf.features.cfg
  #${ODL_HOME}/bin/client feature:install odl-mdsal-clustering
  #${ODL_HOME}/bin/client feature:install odl-jolokia

  echo "Update cluster information statically"
  hm=$(hostname)
  echo "Get current Hostname ${hm}"

  node=($(echo ${hm} | tr '-' '\n'))
  node_name=${node[0]}
  node_index=${node[1]}
  if [ -z ${DOMAIN+x} ]; then
     node_domain=".sdnhost-cluster.onap.svc.cluster.local"
  else
     if [ -z "$DOMAIN" ] ; then
        node_domain=""
     else
        node_domain=".$DOMAIN"
     fi
  fi

  if [ -z $PEER_ODL_CLUSTER ]; then
    echo "This is a local cluster"
    node_list="";
    for ((i=0;i<${SDNC_REPLICAS};i++));
    do
      node_list="${node_list} ${node_name}-$i$node_domain"
    done
    echo "Node list: ${node_list[@]}"
    /opt/opendaylight/current/bin/configure_cluster.sh $((node_index+1)) ${node_list}
  fi

}


ENABLE_ODL_CLUSTER=${ENABLE_ODL_CLUSTER:-false}
SDNC_REPLICAS=${SDNC_REPLICAS:-1}

# ODL jvm Memory adjustments
export JAVA_MIN_MEM=${JAVA_MIN_MEM:-256m}
export JAVA_MAX_MEM=${JAVA_MAX_MEM:-4G}
export JAVA_PERM_MEM=${JAVA_PERM_MEM:-512m}
export JAVA_MAX_PERM_MEM=${JAVA_MAX_PERM_MEM:-1G}

echo "SDNC Settings:"
echo "  ENABLE_ODL_CLUSTER=$ENABLE_ODL_CLUSTER"
echo "  SDNC_REPLICAS=$SDNC_REPLICAS"
echo "  SDNRWT=$SDNRWT"
echo "  JAVA: -Xms${JAVA_MIN_MEM} -Xmx${JAVA_MAX_MEM} -XX:PermSize=${JAVA_PERM_MEM} -XX:MaxPermSize=${JAVA_MAX_PERM_MEM}"

if [ ! -d ${INSTALLED_DIR} ]
then
    mkdir -p ${INSTALLED_DIR}
fi

if [ ! -f ${INSTALLED_DIR}/.installed ]
then
    # Prepare ODL boot configuration and features
    if $ENABLE_ODL_CLUSTER ; then enable_odl_cluster ; fi
    echo "Installed at `date`" > ${INSTALLED_DIR}/.installed
fi


# Start ODL Controller
$ODL_HOME/bin/karaf server
