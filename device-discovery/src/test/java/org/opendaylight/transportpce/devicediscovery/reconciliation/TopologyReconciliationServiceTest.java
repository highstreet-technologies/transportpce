/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.opendaylight.transportpce.devicediscovery.reconciliation.NetconfTopologyRestconfClient.RemoteNode;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;

@ExtendWith(MockitoExtension.class)
public class TopologyReconciliationServiceTest {

    @Mock
    private TopologyWriter topologyWriter;

    @Mock
    private NetconfTopologyRestconfClient restconfClient;

    @Test
    public void testReconcileWithConnectedNodes() {
        DeviceDiscoveryConfig config = createConfig(
                "token",
                List.of(
                    new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests"),
                    new DeviceDiscoveryConfig.ControllerEntry("uuid-2", "https://ctrl-2:8443/rests")
                ));

        RemoteNode device1 = createRemoteNode("device-1", "connected");
        RemoteNode device2 = createRemoteNode("device-2", "connecting");
        RemoteNode device3 = createRemoteNode("device-3", "unable-to-connect");
        RemoteNode device4 = createRemoteNode("device-4", "connected");

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenReturn(List.of(device1, device2, device3));
        when(restconfClient.getNetconfTopology("https://ctrl-2:8443/rests", "token"))
                .thenReturn(List.of(device4));

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(topologyWriter, times(1)).writeNode(device1, "uuid-1");
        verify(topologyWriter, times(1)).writeNode(device2, "uuid-1");
        verify(topologyWriter, times(1)).writeNode(device4, "uuid-2");
        verify(topologyWriter, never()).writeNode(device3, "uuid-1");
    }

    @Test
    public void testReconcileWithNoControllers() {
        DeviceDiscoveryConfig config = createConfig("token", List.of());

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(restconfClient, never()).getNetconfTopology(anyString(), anyString());
        verify(topologyWriter, never()).writeNode(any(RemoteNode.class), anyString());
    }

    @Test
    public void testReconcileWithNullControllers() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setBearerToken("token");
        config.setControllers(null);

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(restconfClient, never()).getNetconfTopology(anyString(), anyString());
    }

    @Test
    public void testReconcileControllerReturnsEmpty() {
        DeviceDiscoveryConfig config = createConfig("token",
                List.of(new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")));

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenReturn(List.of());

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(topologyWriter, never()).writeNode(any(RemoteNode.class), anyString());
    }

    @Test
    public void testReconcileControllerThrowsException() {
        DeviceDiscoveryConfig config = createConfig("token",
                List.of(new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")));

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenThrow(new RuntimeException("Connection refused"));

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(topologyWriter, never()).writeNode(any(RemoteNode.class), anyString());
    }

    @Test
    public void testReconcileSkipsNullConnectionStatus() {
        DeviceDiscoveryConfig config = createConfig("token",
                List.of(new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")));

        RemoteNode nodeWithoutStatus = new RemoteNode();
        nodeWithoutStatus.setNodeId("device-5");
        nodeWithoutStatus.setConnectionStatus(null);

        when(restconfClient.getNetconfTopology("https://ctrl-1:8443/rests", "token"))
                .thenReturn(List.of(nodeWithoutStatus));

        TopologyReconciliationService service =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        service.reconcile();

        verify(topologyWriter, never()).writeNode(any(RemoteNode.class), anyString());
    }

    private DeviceDiscoveryConfig createConfig(String token, List<DeviceDiscoveryConfig.ControllerEntry> controllers) {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setBearerToken(token);
        config.setControllers(controllers);
        return config;
    }

    private RemoteNode createRemoteNode(String nodeId, String connectionStatus) {
        RemoteNode node = new RemoteNode();
        node.setNodeId(nodeId);
        node.setConnectionStatus(connectionStatus);
        return node;
    }
}
