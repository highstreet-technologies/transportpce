/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import org.opendaylight.transportpce.devicediscovery.config.DeviceDiscoveryConfig;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Performs initial netconf-topology reconciliation at startup.
 *
 * For each configured controller, fetches the current netconf-topology via RESTCONF
 * and writes all discovered nodes into the MDSAL operational store with the
 * controller-uuid augmentation. This ensures TransportPCE has an accurate view
 * of all devices even if VES events were missed during downtime.
 *
 * Design rule: netconf-topology from the controller is the Source of Truth.
 * Kafka VES events are real-time triggers; this reconciliation is the safety net.
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
     *
     * Called once at startup. Iterates over all controllers in the config,
     * fetches their netconf-topology, and writes each node into MDSAL with
     * the corresponding controller-uuid.
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

    /**
     * Reconcile a single controller.
     *
     * @param controller the controller entry from config
     * @return number of nodes written
     */
    private int reconcileController(DeviceDiscoveryConfig.ControllerEntry controller) {
        LOG.info("Reconciling controller: uuid={}, baseUrl={}", controller.getUuid(), controller.getBaseUrl());

        List<NetconfTopologyRestconfClient.RemoteNode> remoteNodes =
                restconfClient.getNetconfTopology(controller.getBaseUrl(), config.getBearerToken());

        if (remoteNodes.isEmpty()) {
            LOG.info("No nodes found at controller {}", controller.getUuid());
            return 0;
        }

        int written = 0;
        for (NetconfTopologyRestconfClient.RemoteNode remoteNode : remoteNodes) {
            String connectionStatus = remoteNode.getConnectionStatus();
            // Only write nodes that are connected or connecting — skip disconnected/unable-to-connect
            if (connectionStatus != null
                    && ("connected".equalsIgnoreCase(connectionStatus)
                        || "connecting".equalsIgnoreCase(connectionStatus))) {
                topologyWriter.writeNode(remoteNode.getNodeId(), controller.getUuid(), connectionStatus);
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
