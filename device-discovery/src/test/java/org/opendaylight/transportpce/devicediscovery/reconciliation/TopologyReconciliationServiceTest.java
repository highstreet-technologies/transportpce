/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendaylight.transportpce.devicediscovery.config.DeviceDiscoveryConfig;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;

@ExtendWith(MockitoExtension.class)
class TopologyReconciliationServiceTest {

    @Mock
    private TopologyWriter topologyWriter;

    @Mock
    private NetconfTopologyRestconfClient restconfClient;

    @Test
    void testReconcileWithConnectedNodes() {
        DeviceDiscoveryConfig config = createConfig(
                "token",
                List.of(
                    new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests"),
                    new DeviceDiscoveryConfig.ControllerEntry("uuid-2", "https://ctrl-2:8443/rests")
                ));

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenReturn(List.of(
                    createRemoteNode("device-1", "connected"),
                    createRemoteNode("device-2", "connecting"),
                    createRemoteNode("device-3", "unable-to-connect")
                ));
        when(restconfClient.getNetconfTopology("https://ctrl-2:8443/rests", "token"))
                .thenReturn(List.of(
                    createRemoteNode("device-4", "connected")
                ));

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        // device-1 and device-2 from ctrl-1 should be written (connected + connecting)
        verify(topologyWriter, times(1)).writeNode("device-1", "uuid-1", "connected");
        verify(topologyWriter, times(1)).writeNode("device-2", "uuid-1", "connecting");
        // device-3 is unable-to-connect — should NOT be written
        verify(topologyWriter, never()).writeNode("device-3", "uuid-1", "unable-to-connect");
        // device-4 from ctrl-2 should be written
        verify(topologyWriter, times(1)).writeNode("device-4", "uuid-2", "connected");
    }

    @Test
    void testReconcileWithNoControllers() {
        DeviceDiscoveryConfig config = createConfig("token", List.of());

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(restconfClient, never()).getNetconfTopology(anyString(), anyString());
        verify(topologyWriter, never()).writeNode(anyString(), anyString(), anyString());
    }

    @Test
    void testReconcileWithNullControllers() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setBearerToken("token");
        config.setControllers(null);

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(restconfClient, never()).getNetconfTopology(anyString(), anyString());
    }

    @Test
    void testReconcileControllerReturnsEmpty() {
        DeviceDiscoveryConfig config = createConfig("token",
                List.of(new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")));

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenReturn(List.of());

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(topologyWriter, never()).writeNode(anyString(), anyString(), anyString());
    }

    @Test
    void testReconcileControllerThrowsException() {
        DeviceDiscoveryConfig config = createConfig("token",
                List.of(new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")));

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenThrow(new RuntimeException("Connection refused"));

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        // Should not propagate exception
        service.reconcile();

        verify(topologyWriter, never()).writeNode(anyString(), anyString(), anyString());
    }

    @Test
    void testReconcileSkipsNullConnectionStatus() {
        DeviceDiscoveryConfig config = createConfig("token",
                List.of(new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")));

        NetconfTopologyRestconfClient.RemoteNode nodeWithoutStatus = new NetconfTopologyRestconfClient.RemoteNode();
        nodeWithoutStatus.setNodeId("device-5");
        nodeWithoutStatus.setConnectionStatus(null);

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenReturn(List.of(nodeWithoutStatus));

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(topologyWriter, never()).writeNode(anyString(), anyString(), anyString());
    }

    private DeviceDiscoveryConfig createConfig(String token, List<DeviceDiscoveryConfig.ControllerEntry> controllers) {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setBearerToken(token);
        config.setControllers(controllers);
        return config;
    }

    private NetconfTopologyRestconfClient.RemoteNode createRemoteNode(String nodeId, String connectionStatus) {
        NetconfTopologyRestconfClient.RemoteNode node = new NetconfTopologyRestconfClient.RemoteNode();
        node.setNodeId(nodeId);
        node.setConnectionStatus(connectionStatus);
        return node;
    }
}
