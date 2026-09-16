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
 * RESTCONF client for the {@code network-topology} model (TBD namespace).
 *
 * <p>Fetches nodes from {@code /data/network-topology:network-topology/topology=topology-netconf}
 * and parses the {@code netconf-node-topology:netconf-node} augmentation.
 */
public class NetconfTopologyRestconfClient extends AbstractTopologyRestconfClient {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfTopologyRestconfClient.class);

    private static final String TOPOLOGY_PATH =
            "/data/network-topology:network-topology/topology=topology-netconf";
    private static final String NODE_PATH_PREFIX =
            "/data/network-topology:network-topology/topology=topology-netconf/node=";

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
            JsonNode topologyArray = root.path("network-topology:topology");

            if (!topologyArray.isArray() || topologyArray.isEmpty()) {
                LOG.warn("No topology array in response");
                return nodes;
            }

            JsonNode topology = topologyArray.get(0);
            JsonNode nodeArray = topology.path("node");

            if (!nodeArray.isArray()) {
                LOG.warn("No node array in topology");
                return nodes;
            }

            for (JsonNode nodeJson : nodeArray) {
                RemoteNode node = parseNodeJson(nodeJson);
                if (node != null) {
                    nodes.add(node);
                }
            }

            LOG.info("Parsed {} nodes from network-topology response", nodes.size());
        } catch (Exception e) {
            LOG.error("Failed to parse topology JSON", e);
        }
        return nodes;
    }

    @Override
    protected Optional<RemoteNode> parseNodeResponse(String jsonBody) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode nodeArray = root.path("network-topology:node");

            if (nodeArray.isArray() && !nodeArray.isEmpty()) {
                RemoteNode node = parseNodeJson(nodeArray.get(0));
                if (node != null) {
                    LOG.info("Parsed node {} from network-topology single-node response", node.getNodeId());
                    return Optional.of(node);
                }
            }

            LOG.warn("No node found in single-node response");
            return Optional.empty();
        } catch (Exception e) {
            LOG.error("Failed to parse single-node JSON", e);
            return Optional.empty();
        }
    }

    /**
     * Parse a single node JSON object into a {@link RemoteNode}.
     *
     * <p>Extracts fields from the {@code netconf-node-topology:netconf-node} augmentation.
     */
    private RemoteNode parseNodeJson(JsonNode nodeJson) {
        String nodeId = nodeJson.path("node-id").asText(null);
        if (nodeId == null || nodeId.isBlank()) {
            return null;
        }

        RemoteNode remoteNode = new RemoteNode();
        remoteNode.setNodeId(nodeId);

        JsonNode netconfNode = nodeJson.path("netconf-node-topology:netconf-node");
        if (!netconfNode.isMissingNode()) {
            remoteNode.setConnectionStatus(netconfNode.path("connection-status").asText(null));
            remoteNode.setHost(netconfNode.path("host").asText(null));
            remoteNode.setPort(netconfNode.path("port").asInt(0));
            remoteNode.setSessionId(netconfNode.path("session-id").asLong(0));
            remoteNode.setConnectedMessage(netconfNode.path("connected-message").asText(null));

            JsonNode availCaps = netconfNode.path("available-capabilities")
                    .path("available-capability");
            List<String> capabilities = new ArrayList<>();
            if (availCaps.isArray()) {
                for (JsonNode cap : availCaps) {
                    String capValue = cap.path("capability").asText(null);
                    if (capValue != null && !capValue.isBlank()) {
                        capabilities.add(capValue);
                    }
                }
            }
            remoteNode.setAvailableCapabilities(capabilities);

            JsonNode unavailCaps = netconfNode.path("unavailable-capabilities")
                    .path("unavailable-capability");
            List<String> unavailableCapabilities = new ArrayList<>();
            if (unavailCaps.isArray()) {
                for (JsonNode cap : unavailCaps) {
                    String capValue = cap.path("capability").asText(null);
                    if (capValue != null && !capValue.isBlank()) {
                        unavailableCapabilities.add(capValue);
                    }
                }
            }
            remoteNode.setUnavailableCapabilities(unavailableCapabilities);
        }

        LOG.debug("Parsed node: id={}, status={}, capabilities={}",
                remoteNode.getNodeId(),
                remoteNode.getConnectionStatus(),
                remoteNode.getAvailableCapabilities().size());
        return remoteNode;
    }
}
