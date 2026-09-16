/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import java.util.ArrayList;
import java.util.List;

/**
 * Data holder for a remote node parsed from a RESTCONF topology response.
 *
 * <p>Contains the full node information needed to write it into MDSAL, regardless of whether
 * the source is network-topology or ietf-network model. The fields are populated by the
 * specific topology client's parser.
 */
public class RemoteNode {

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
        return "connected".equalsIgnoreCase(this.connectionStatus)
                || "Mounted".equalsIgnoreCase(this.connectionStatus)
                || "Connected".equalsIgnoreCase(this.connectionStatus);
    }

    public boolean hasTrpceCapabilities() {
        var caps = this.getAvailableCapabilities();
        if (caps == null || caps.isEmpty()) {
            return false;
        }
        return caps.parallelStream().anyMatch(e -> e.contains("org-openroadm-device"));
    }
}
