/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.opendaylight.transportpce.devicediscovery.config.TransportPceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base for RESTCONF topology clients that fetch device nodes from an OpenDaylight controller.
 *
 * <p>Provides the common HTTP infrastructure (Jersey client, Bearer token authentication, error handling)
 * and defines template methods for the topology-specific concerns:
 * <ul>
 *   <li>{@link #buildTopologyUrl(String)} — the RESTCONF path for fetching all nodes</li>
 *   <li>{@link #buildNodeUrl(String, String)} — the RESTCONF path for fetching a single node</li>
 *   <li>{@link #parseTopologyResponse(String)} — deserialize the full-topology JSON into {@link RemoteNode} list</li>
 *   <li>{@link #parseNodeResponse(String)} — deserialize the single-node JSON into a {@link RemoteNode}</li>
 * </ul>
 *
 * <p>Concrete implementations: {@link NetconfTopologyRestconfClient} (network-topology model),
 * {@link IetfNetworkRestconfClient} (ietf-network model with unm-network-topology augmentation).
 */
public abstract class AbstractTopologyRestconfClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTopologyRestconfClient.class);

    protected final Client client;

    protected AbstractTopologyRestconfClient() {
        this.client = ClientBuilder.newClient();
    }

    public static AbstractTopologyRestconfClient create(TransportPceConfig config) {
        if(config.isIetfNetworkTopology()){
            return new IetfNetworkRestconfClient();
        }
        else if(config.isNetconfTopology()){
            return new NetconfTopologyRestconfClient();
        }

        throw new IllegalArgumentException("unable to create rest client for config url "+ config.getMountPrefix());
    }

    /**
     * Build the RESTCONF URL for fetching all topology nodes from a controller.
     *
     * @param baseUrl the controller base URL (e.g. {@code http://controller-1:8181/rests})
     * @return the full RESTCONF URL
     */
    protected abstract String buildTopologyUrl(String baseUrl);

    /**
     * Build the RESTCONF URL for fetching a single node by node-id from a controller.
     *
     * @param baseUrl the controller base URL
     * @param nodeId  the device node-id
     * @return the full RESTCONF URL
     */
    protected abstract String buildNodeUrl(String baseUrl, String nodeId);

    /**
     * Parse the JSON response from a full-topology GET into a list of {@link RemoteNode}.
     *
     * @param jsonBody the raw JSON response body
     * @return parsed node descriptors (empty list on failure)
     */
    protected abstract List<RemoteNode> parseTopologyResponse(String jsonBody);

    /**
     * Parse the JSON response from a single-node GET into a {@link RemoteNode}.
     *
     * @param jsonBody the raw JSON response body
     * @return parsed node descriptor, or empty if not found
     */
    protected abstract Optional<RemoteNode> parseNodeResponse(String jsonBody);

    /**
     * Fetch all topology nodes from a controller's operational datastore.
     *
     * @param baseUrl     the base URL of the controller
     * @param bearerToken the shared Bearer token for authentication
     * @return list of node descriptors extracted from the topology response
     */
    public List<RemoteNode> getTopology(String baseUrl, String bearerToken) {
        String url = UriBuilder.fromUri(baseUrl)
                .path(buildTopologyUrl(baseUrl))
                .queryParam("content", "nonconfig")
                .build()
                .toString();

        LOG.info("Fetching topology from controller: {}", url);

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
            return parseTopologyResponse(body);
        } catch (Exception e) {
            LOG.error("Error fetching topology from {}", baseUrl, e);
            return List.of();
        }
    }

    /**
     * Fetch a single node from a controller by node-id.
     *
     * @param nodeId      the device node-id to fetch
     * @param baseUrl     the base URL of the controller
     * @param bearerToken the shared Bearer token for authentication
     * @return the node descriptor, or empty if not found
     */
    public Optional<RemoteNode> getNode(String nodeId, String baseUrl, String bearerToken) {
        String url = UriBuilder.fromUri(baseUrl)
                .path(buildNodeUrl(baseUrl, nodeId))
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
            return parseNodeResponse(body);
        } catch (Exception e) {
            LOG.error("Error fetching node {} from {}", nodeId, baseUrl, e);
            return Optional.empty();
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
