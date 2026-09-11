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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTCONF client for querying netconf-topology from an OpenDaylight controller.
 * <p>
 * Performs GET /data/network-topology:network-topology/topology=topology-netconf with content=nonconfig to retrieve the
 * operational state of all netconf nodes. Also supports fetching a single node by node-id.
 * <p>
 * Uses Bearer token authentication (shared across all controllers).
 */
public class NetconfTopologyRestconfClient {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfTopologyRestconfClient.class);

    private static final String TOPOLOGY_PATH =
            "/data/network-topology:network-topology/topology=topology-netconf";
    private static final String NODE_PATH_PREFIX =
            "/data/network-topology:network-topology/topology=topology-netconf/node=";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Client client;

    public NetconfTopologyRestconfClient() {
        this.client = ClientBuilder.newClient();
    }

    /**
     * Fetch all netconf nodes from a controller's operational datastore.
     *
     * @param baseUrl     the base URL of the controller (e.g. http://controller-1:8181/rests)
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
     * Fetch a single netconf node from a controller by node-id.
     *
     * @param nodeId      the device node-id to fetch
     * @param baseUrl     the base URL of the controller (e.g. http://controller-1:8181/rests)
     * @param bearerToken the shared Bearer token for authentication
     * @return the node descriptor, or empty if not found
     */
    public Optional<RemoteNode> getNode(String nodeId, String baseUrl, String bearerToken) {
        String url = UriBuilder.fromUri(baseUrl)
                .path(NODE_PATH_PREFIX + nodeId)
                .queryParam("content", "nonconfig")
                .build()
                .toString();

        LOG.info("Fetching node {} from controller: {}", nodeId, url);

        Invocation.Builder request = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        try (Response response = request.get()) {
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                LOG.error("Failed to fetch node {} from {}: HTTP {}", nodeId, baseUrl, response.getStatus());
                return Optional.empty();
            }

            String body = response.readEntity(String.class);
            return parseSingleNode(body, baseUrl);

        } catch (Exception e) {
            LOG.error("Error fetching node {} from {}", nodeId, baseUrl, e);
            return Optional.empty();
        }
    }

    /**
     * Parse the RESTCONF topology response and extract node information.
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
                RemoteNode node = parseNodeJson(nodeJson);
                if (node != null) {
                    nodes.add(node);
                }
            }

            LOG.info("Parsed {} nodes from topology at {}", nodes.size(), sourceUrl);

        } catch (Exception e) {
            LOG.error("Failed to parse topology JSON from {}", sourceUrl, e);
        }
        return nodes;
    }

    /**
     * Parse a single node response (from GET node={node-id}).
     */
    Optional<RemoteNode> parseSingleNode(String jsonBody, String sourceUrl) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode nodeArray = root.path("network-topology:node");

            if (nodeArray.isArray() && !nodeArray.isEmpty()) {
                RemoteNode node = parseNodeJson(nodeArray.get(0));
                if (node != null) {
                    LOG.info("Parsed node {} from {}", node.getNodeId(), sourceUrl);
                    return Optional.of(node);
                }
            }

            LOG.warn("No node found in single-node response from {}", sourceUrl);
            return Optional.empty();

        } catch (Exception e) {
            LOG.error("Failed to parse single-node JSON from {}", sourceUrl, e);
            return Optional.empty();
        }
    }

    /**
     * Parse a single node JSON object into a RemoteNode.
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

            // Extract unavailable capabilities
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

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Data holder for a remote netconf node parsed from RESTCONF response. Contains the full node information needed to
     * write it into MDSAL.
     */
    public static class RemoteNode {

        private String nodeId;
        private String connectionStatus;
        private String host;
        private int port;
        private long sessionId;
        private String connectedMessage;
        private List<String> availableCapabilities = new ArrayList<>();
        private List<String> unavailableCapabilities = new ArrayList<>();

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

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public long getSessionId() {
            return sessionId;
        }

        public void setSessionId(long sessionId) {
            this.sessionId = sessionId;
        }

        public String getConnectedMessage() {
            return connectedMessage;
        }

        public void setConnectedMessage(String connectedMessage) {
            this.connectedMessage = connectedMessage;
        }

        public List<String> getAvailableCapabilities() {
            return availableCapabilities;
        }

        public void setAvailableCapabilities(List<String> availableCapabilities) {
            this.availableCapabilities = availableCapabilities;
        }

        public List<String> getUnavailableCapabilities() {
            return unavailableCapabilities;
        }

        public void setUnavailableCapabilities(List<String> unavailableCapabilities) {
            this.unavailableCapabilities = unavailableCapabilities;
        }

        @Override
        public String toString() {
            return "RemoteNode{nodeId='" + nodeId + "', status='" + connectionStatus
                    + "', host='" + host + "', port=" + port
                    + ", capabilities=" + availableCapabilities.size() + "}";
        }

        public boolean isConnected() {
            return "connected".equalsIgnoreCase(this.connectionStatus);
        }

        public boolean hasTrpceCapabilities() {

            var caps = this.getAvailableCapabilities();
            if (caps == null || caps.size() <= 0) {
                return false;
            }
            return caps.parallelStream().anyMatch(e -> e.contains("org-openroadm-device"));
        }

    }
}
