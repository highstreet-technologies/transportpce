/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTCONF client for the {@code ietf-network} model with {@code unm-network-topology} augmentation.
 *
 * <p>Fetches nodes from {@code /rests/data/ietf-network:networks/network={network-id}}
 * and parses the {@code unm-network-topology} augmentation for connection state, host, port,
 * capabilities, controller-id and other device metadata.
 *
 * <p>The connection-state enum values from this model differ from network-topology:
 * {@code Mounted}, {@code Unmounted}, {@code Connecting}, {@code Connected},
 * {@code UnableToConnect}, {@code Disconnected}, {@code Removed}, {@code Undefined}.
 */
public class IetfNetworkRestconfClient extends AbstractTopologyRestconfClient {

    private static final Logger LOG = LoggerFactory.getLogger(IetfNetworkRestconfClient.class);

    private static final String TOPOLOGY_PATH =
            "/data/ietf-network:networks/network=unm-topology";
    private static final String NODE_PATH_PREFIX =
            "/data/ietf-network:networks/network=unm-topology/node=";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected String buildTopologyUrl(String baseUrl) {
        return TOPOLOGY_PATH;
    }

    @Override
    protected String buildNodeUrl(String baseUrl, String nodeId) {
        return NODE_PATH_PREFIX + nodeId;
    }

    @Override
    protected List<RemoteNode> parseTopologyResponse(String jsonBody) {
        List<RemoteNode> nodes = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode networkArray = root.path("ietf-network:network");

            if (!networkArray.isArray() || networkArray.isEmpty()) {
                LOG.warn("No network array in ietf-network response");
                return nodes;
            }

            JsonNode network = networkArray.get(0);
            JsonNode nodeArray = network.path("node");

            if (!nodeArray.isArray()) {
                LOG.warn("No node array in ietf-network");
                return nodes;
            }

            for (JsonNode nodeJson : nodeArray) {
                RemoteNode node = parseNodeJson(nodeJson);
                if (node != null) {
                    nodes.add(node);
                }
            }

            LOG.info("Parsed {} nodes from ietf-network response", nodes.size());
        } catch (Exception e) {
            LOG.error("Failed to parse ietf-network topology JSON", e);
        }
        return nodes;
    }

    @Override
    protected Optional<RemoteNode> parseNodeResponse(String jsonBody) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode nodeArray = root.path("ietf-network:node");

            if (nodeArray.isArray() && !nodeArray.isEmpty()) {
                RemoteNode node = parseNodeJson(nodeArray.get(0));
                if (node != null) {
                    LOG.info("Parsed node {} from ietf-network single-node response", node.getNodeId());
                    return Optional.of(node);
                }
            }

            LOG.warn("No node found in ietf-network single-node response");
            return Optional.empty();
        } catch (Exception e) {
            LOG.error("Failed to parse ietf-network single-node JSON", e);
            return Optional.empty();
        }
    }

    /**
     * Parse a single node JSON object into a {@link RemoteNode}.
     *
     * <p>Extracts fields from the {@code unm-network-topology} augmentation:
     * connection-state, host, port, available-capabilities, non-available-capabilities,
     * controller-id, device-function, is-active, site, geolocation.
     */
    private RemoteNode parseNodeJson(JsonNode nodeJson) {
        String nodeId = nodeJson.path("node-id").asText(null);
        if (nodeId == null || nodeId.isBlank()) {
            return null;
        }

        RemoteNode remoteNode = new RemoteNode();
        remoteNode.setNodeId(nodeId);

        // unm-network-topology augmentation fields (prefixed with "unm-network-topology:")
        remoteNode.setConnectionStatus(
                nodeJson.path("unm-network-topology:connection-state").asText(null));
        remoteNode.setHost(
                nodeJson.path("unm-network-topology:host").asText(null));
        remoteNode.setPort(
                nodeJson.path("unm-network-topology:port").asInt(0));

        // Available capabilities
        JsonNode availCaps = nodeJson.path("unm-network-topology:available-capabilities");
        List<String> capabilities = new ArrayList<>();
        if (availCaps.isArray()) {
            for (JsonNode cap : availCaps) {
                String capValue = cap.asText(null);
                if (capValue != null && !capValue.isBlank()) {
                    capabilities.add(capValue);
                }
            }
        }
        remoteNode.setAvailableCapabilities(capabilities);

        // Unavailable capabilities
        JsonNode unavailCaps = nodeJson.path("unm-network-topology:non-available-capabilities");
        List<String> unavailableCapabilities = new ArrayList<>();
        if (unavailCaps.isArray()) {
            for (JsonNode cap : unavailCaps) {
                String capValue = cap.asText(null);
                if (capValue != null && !capValue.isBlank()) {
                    unavailableCapabilities.add(capValue);
                }
            }
        }
        remoteNode.setUnavailableCapabilities(unavailableCapabilities);

        // Controller-id (which controller holds the connection)
        String controllerId = nodeJson.path("unm-network-topology:controller-id").asText(null);
        if (controllerId != null && !controllerId.isBlank()) {
            LOG.debug("Node {} has controller-id: {}", nodeId, controllerId);
        }

        LOG.debug("Parsed ietf-network node: id={}, status={}, capabilities={}",
                remoteNode.getNodeId(),
                remoteNode.getConnectionStatus(),
                remoteNode.getAvailableCapabilities().size());
        return remoteNode;
    }
}
