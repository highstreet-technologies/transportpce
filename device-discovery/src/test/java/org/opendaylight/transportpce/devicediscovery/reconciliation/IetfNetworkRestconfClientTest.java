/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class IetfNetworkRestconfClientTest {

    private static final String NETWORK_ID = "topology-netconf";

    @Test
    void testParseTopologyWithMountedNodes() {
        String json = """
            {
              "ietf-network:network": [
                {
                  "network-id": "topology-netconf",
                  "node": [
                    {
                      "node-id": "device-1",
                      "unm-network-topology:connection-state": "Mounted",
                      "unm-network-topology:host": "10.0.0.1",
                      "unm-network-topology:port": 830,
                      "unm-network-topology:available-capabilities": [
                        "(org-openroadm-device?revision=2020-05-29)org-openroadm-device",
                        "(urn:ietf:params:xml:ns:yang:ietf-interfaces?revision=2018-02-20)ietf-interfaces"
                      ],
                      "unm-network-topology:non-available-capabilities": [
                        "urn:ietf:params:xml:ns:yang:ietf-yang-types?revision=2013-07-15"
                      ],
                      "unm-network-topology:controller-id": "c94bbd5f-d456-44bd-aa7a-47b2e4f73253"
                    },
                    {
                      "node-id": "device-2",
                      "unm-network-topology:connection-state": "Connecting"
                    }
                  ]
                }
              ]
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        List<RemoteNode> nodes = client.parseTopologyResponse(json);

        assertEquals(2, nodes.size());

        assertEquals("device-1", nodes.get(0).getNodeId());
        assertEquals("Mounted", nodes.get(0).getConnectionStatus());
        assertEquals("10.0.0.1", nodes.get(0).getHost());
        assertEquals(830, nodes.get(0).getPort());
        assertEquals(2, nodes.get(0).getAvailableCapabilities().size());
        assertEquals(1, nodes.get(0).getUnavailableCapabilities().size());
        assertTrue(nodes.get(0).isConnected());
        assertTrue(nodes.get(0).hasTrpceCapabilities());

        assertEquals("device-2", nodes.get(1).getNodeId());
        assertEquals("Connecting", nodes.get(1).getConnectionStatus());
        assertTrue(nodes.get(1).getAvailableCapabilities().isEmpty());

        client.close();
    }

    @Test
    void testParseTopologyWithDisconnectedNode() {
        String json = """
            {
              "ietf-network:network": [
                {
                  "network-id": "topology-netconf",
                  "node": [
                    {
                      "node-id": "device-3",
                      "unm-network-topology:connection-state": "UnableToConnect"
                    }
                  ]
                }
              ]
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        List<RemoteNode> nodes = client.parseTopologyResponse(json);

        assertEquals(1, nodes.size());
        assertEquals("device-3", nodes.get(0).getNodeId());
        assertEquals("UnableToConnect", nodes.get(0).getConnectionStatus());
        assertTrue(nodes.get(0).getAvailableCapabilities().isEmpty());

        client.close();
    }

    @Test
    void testParseTopologyEmpty() {
        String json = """
            {
              "ietf-network:network": []
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        List<RemoteNode> nodes = client.parseTopologyResponse(json);

        assertTrue(nodes.isEmpty());
        client.close();
    }

    @Test
    void testParseTopologyNoNodeArray() {
        String json = """
            {
              "ietf-network:network": [
                {
                  "network-id": "topology-netconf"
                }
              ]
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        List<RemoteNode> nodes = client.parseTopologyResponse(json);

        assertTrue(nodes.isEmpty());
        client.close();
    }

    @Test
    void testParseTopologyInvalidJson() {
        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        List<RemoteNode> nodes = client.parseTopologyResponse("not valid json");

        assertTrue(nodes.isEmpty());
        client.close();
    }

    @Test
    void testParseTopologyNodeWithoutAugmentation() {
        String json = """
            {
              "ietf-network:network": [
                {
                  "network-id": "topology-netconf",
                  "node": [
                    {
                      "node-id": "device-4"
                    }
                  ]
                }
              ]
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        List<RemoteNode> nodes = client.parseTopologyResponse(json);

        assertEquals(1, nodes.size());
        assertEquals("device-4", nodes.get(0).getNodeId());
        assertTrue(nodes.get(0).getAvailableCapabilities().isEmpty());
        assertTrue(nodes.get(0).getUnavailableCapabilities().isEmpty());

        client.close();
    }

    @Test
    void testParseNodeResponse() {
        String json = """
            {
              "ietf-network:node": [
                {
                  "node-id": "device-1",
                  "unm-network-topology:connection-state": "Connected",
                  "unm-network-topology:host": "10.0.0.1",
                  "unm-network-topology:port": 17830,
                  "unm-network-topology:available-capabilities": [
                    "(org-openroadm-device?revision=2020-05-29)org-openroadm-device"
                  ],
                  "unm-network-topology:controller-id": "c94bbd5f-d456-44bd-aa7a-47b2e4f73253"
                }
              ]
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        Optional<RemoteNode> nodeOpt = client.parseNodeResponse(json);

        assertTrue(nodeOpt.isPresent());
        RemoteNode node = nodeOpt.orElseThrow();
        assertEquals("device-1", node.getNodeId());
        assertEquals("Connected", node.getConnectionStatus());
        assertEquals("10.0.0.1", node.getHost());
        assertEquals(17830, node.getPort());
        assertEquals(1, node.getAvailableCapabilities().size());
        assertTrue(node.isConnected());
        assertTrue(node.hasTrpceCapabilities());

        client.close();
    }

    @Test
    void testParseNodeResponseEmpty() {
        String json = """
            {
              "ietf-network:node": []
            }
            """;

        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        Optional<RemoteNode> nodeOpt = client.parseNodeResponse(json);

        assertTrue(nodeOpt.isEmpty());
        client.close();
    }

    @Test
    void testParseNodeResponseInvalidJson() {
        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient(NETWORK_ID);
        Optional<RemoteNode> nodeOpt = client.parseNodeResponse("not valid json");

        assertTrue(nodeOpt.isEmpty());
        client.close();
    }

    @Test
    void testBuildTopologyUrl() {
        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient("openroadm-network");
        String path = client.buildTopologyUrl("http://ctrl:8181/rests");
        assertEquals("/rests/data/ietf-network:networks/network=openroadm-network", path);
    }

    @Test
    void testBuildNodeUrl() {
        IetfNetworkRestconfClient client = new IetfNetworkRestconfClient("openroadm-network");
        String path = client.buildNodeUrl("http://ctrl:8181/rests", "device-1");
        assertEquals("/rests/data/ietf-network:networks/network=openroadm-network/node=device-1", path);
    }
}
