/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import java.util.List;
import org.opendaylight.transportpce.devicediscovery.config.DeviceDiscoveryConfig;
import org.opendaylight.transportpce.devicediscovery.reconciliation.NetconfTopologyRestconfClient.RemoteNode;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs initial netconf-topology reconciliation at startup.
 *
 * For each configured controller, fetches the current netconf-topology via RESTCONF
 * and writes all discovered nodes with full data into the MDSAL operational store
 * with the controller-uuid augmentation.
 */
public class TopologyReconciliationService {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyReconciliationService.class);

    private final DeviceDiscoveryConfig config;
    private final TopologyWriter topologyWriter;
    private final NetconfTopologyRestconfClient restconfClient;

    public TopologyReconciliationService(DeviceDiscoveryConfig config, TopologyWriter topologyWriter,
            NetconfTopologyRestconfClient restconfClient) {
        this.config = config;
        this.topologyWriter = topologyWriter;
        this.restconfClient = restconfClient;
    }

    /**
     * Reconcile netconf-topology from all configured controllers.
     */
    public void reconcile() {
        List<DeviceDiscoveryConfig.ControllerEntry> controllers = config.getControllers();
        if (controllers == null || controllers.isEmpty()) {
            LOG.warn("No controllers configured, skipping reconciliation");
            return;
        }

        LOG.info("Starting topology reconciliation from {} controller(s)", controllers.size());

        int totalNodes = 0;
        for (DeviceDiscoveryConfig.ControllerEntry controller : controllers) {
            try {
                totalNodes += reconcileController(controller);
            } catch (Exception e) {
                LOG.error("Failed to reconcile controller {} ({})", controller.getUuid(), controller.getBaseUrl(), e);
            }
        }

        LOG.info("Topology reconciliation complete — {} nodes written across {} controller(s)",
                totalNodes, controllers.size());
    }

    private int reconcileController(DeviceDiscoveryConfig.ControllerEntry controller) {
        LOG.info("Reconciling controller: uuid={}, baseUrl={}", controller.getUuid(), controller.getBaseUrl());

        List<RemoteNode> remoteNodes =
                restconfClient.getNetconfTopology(controller.getBaseUrl(), config.getBearerToken());

        if (remoteNodes.isEmpty()) {
            LOG.info("No nodes found at controller {}", controller.getUuid());
            return 0;
        }

        int written = 0;
        for (RemoteNode remoteNode : remoteNodes) {
            String connectionStatus = remoteNode.getConnectionStatus();
            if (connectionStatus != null
                    && ("connected".equalsIgnoreCase(connectionStatus)
                        || "connecting".equalsIgnoreCase(connectionStatus))) {
                topologyWriter.writeNode(remoteNode, controller.getUuid());
                written++;
            } else {
                LOG.debug("Skipping node {} from controller {} — connection status: {}",
                        remoteNode.getNodeId(), controller.getUuid(), connectionStatus);
            }
        }

        LOG.info("Reconciled {} nodes from controller {}", written, controller.getUuid());
        return written;
    }
}
