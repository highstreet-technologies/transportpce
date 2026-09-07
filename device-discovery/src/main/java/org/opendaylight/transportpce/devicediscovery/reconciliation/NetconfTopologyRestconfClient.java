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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * RESTCONF client for querying netconf-topology from an OpenDaylight controller.
 *
 * Performs GET /rests/data/network-topology:network-topology/topology=topology-netconf
 * with content=nonconfig to retrieve the operational state of all netconf nodes.
 *
 * Uses Bearer token authentication (shared across all controllers).
 */
public class NetconfTopologyRestconfClient {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfTopologyRestconfClient.class);

    private static final String TOPOLOGY_PATH =
            "/rests/data/network-topology:network-topology/topology=topology-netconf";
    private static final String TOPOLOGY_NETCONF = "topology-netconf";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Client client;

    public NetconfTopologyRestconfClient() {
        this.client = ClientBuilder.newClient();
    }

    /**
     * Fetch all netconf nodes from a controller's operational datastore.
     *
     * @param baseUrl the base URL of the controller (e.g. https://controller-1:8443)
     * @param bearerToken the shared Bearer token for authentication
     * @return list of node descriptors extracted from the topology response
     */
    public List<RemoteNode> getNetconfTopology(String baseUrl, String bearerToken) {
        String url = UriBuilder.fromUri(baseUrl)
                .path(TOPOLOGY_PATH)
                .queryParam("content", "nonconfig")
                .build()
                .toString();

        LOG.info("Fetching netconf-topology from controller: {}", url);

        Invocation.Builder request = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        try (Response response = request.get()) {
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                LOG.error("Failed to fetch topology from {}: HTTP {}", baseUrl, response.getStatus());
                return List.of();
            }

            String body = response.readEntity(String.class);
            return parseTopology(body, baseUrl);

        } catch (Exception e) {
            LOG.error("Error fetching topology from {}", baseUrl, e);
            return List.of();
        }
    }

    /**
     * Parse the RESTCONF topology response and extract node information.
     *
     * Expected JSON structure:
     * {
     *   "network-topology:topology": [{
     *     "topology-id": "topology-netconf",
     *     "node": [
     *       {
     *         "node-id": "device-1",
     *         "netconf-node-topology:netconf-node": {
     *           "connection-status": "connected",
     *           "available-capabilities": { ... }
     *         }
     *       }
     *     ]
     *   }]
     * }
     */
    List<RemoteNode> parseTopology(String jsonBody, String sourceUrl) {
        List<RemoteNode> nodes = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode topologyArray = root.path("network-topology:topology");

            if (!topologyArray.isArray() || topologyArray.isEmpty()) {
                LOG.warn("No topology array in response from {}", sourceUrl);
                return nodes;
            }

            JsonNode topology = topologyArray.get(0);
            JsonNode nodeArray = topology.path("node");

            if (!nodeArray.isArray()) {
                LOG.warn("No node array in topology from {}", sourceUrl);
                return nodes;
            }

            for (JsonNode nodeJson : nodeArray) {
                String nodeId = nodeJson.path("node-id").asText(null);
                if (nodeId == null || nodeId.isBlank()) {
                    continue;
                }

                RemoteNode remoteNode = new RemoteNode();
                remoteNode.setNodeId(nodeId);

                JsonNode netconfNode = nodeJson.path("netconf-node-topology:netconf-node");
                if (!netconfNode.isMissingNode()) {
                    String connectionStatus = netconfNode.path("connection-status").asText(null);
                    remoteNode.setConnectionStatus(connectionStatus);

                    // Extract available capabilities
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
                }

                nodes.add(remoteNode);
                LOG.debug("Parsed node: id={}, status={}, capabilities={}",
                        remoteNode.getNodeId(),
                        remoteNode.getConnectionStatus(),
                        remoteNode.getAvailableCapabilities().size());
            }

            LOG.info("Parsed {} nodes from topology at {}", nodes.size(), sourceUrl);

        } catch (Exception e) {
            LOG.error("Failed to parse topology JSON from {}", sourceUrl, e);
        }
        return nodes;
    }

    /**
     * Close the underlying JAX-RS client.
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Simple data holder for a remote netconf node parsed from RESTCONF response.
     */
    public static class RemoteNode {
        private String nodeId;
        private String connectionStatus;
        private List<String> availableCapabilities = new ArrayList<>();

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getConnectionStatus() {
            return connectionStatus;
        }

        public void setConnectionStatus(String connectionStatus) {
            this.connectionStatus = connectionStatus;
        }

        public List<String> getAvailableCapabilities() {
            return availableCapabilities;
        }

        public void setAvailableCapabilities(List<String> availableCapabilities) {
            this.availableCapabilities = availableCapabilities;
        }

        @Override
        public String toString() {
            return "RemoteNode{nodeId='" + nodeId + "', status='"
                    + connectionStatus + "', capabilities=" + availableCapabilities.size() + "}";
        }
    }
}
