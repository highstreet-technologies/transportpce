/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class NetconfTopologyRestconfClientTest {

    @Test
    void testParseTopologyWithConnectedNodes() {
        String json = """
            {
              "network-topology:topology": [
                {
                  "topology-id": "topology-netconf",
                  "node": [
                    {
                      "node-id": "device-1",
                      "netconf-node-topology:netconf-node": {
                        "connection-status": "connected",
                        "available-capabilities": {
                          "available-capability": [
                            { "capability": "(org-openroadm-device?revision=2020-05-29)org-openroadm-device" },
                            { "capability": "(urn:ietf:params:xml:ns:yang:ietf-interfaces?revision=2018-02-20)ietf-interfaces" }
                          ]
                        }
                      }
                    },
                    {
                      "node-id": "device-2",
                      "netconf-node-topology:netconf-node": {
                        "connection-status": "connecting"
                      }
                    }
                  ]
                }
              ]
            }
            """;

        NetconfTopologyRestconfClient client = new NetconfTopologyRestconfClient();
        List<NetconfTopologyRestconfClient.RemoteNode> nodes = client.parseTopology(json, "test-url");

        assertEquals(2, nodes.size());
        assertEquals("device-1", nodes.get(0).getNodeId());
        assertEquals("connected", nodes.get(0).getConnectionStatus());
        assertEquals(2, nodes.get(0).getAvailableCapabilities().size());
        assertEquals("device-2", nodes.get(1).getNodeId());
        assertEquals("connecting", nodes.get(1).getConnectionStatus());
        assertTrue(nodes.get(1).getAvailableCapabilities().isEmpty());

        client.close();
    }

    @Test
    void testParseTopologyWithDisconnectedNode() {
        String json = """
            {
              "network-topology:topology": [
                {
                  "topology-id": "topology-netconf",
                  "node": [
                    {
                      "node-id": "device-3",
                      "netconf-node-topology:netconf-node": {
                        "connection-status": "unable-to-connect"
                      }
                    }
                  ]
                }
              ]
            }
            """;

        NetconfTopologyRestconfClient client = new NetconfTopologyRestconfClient();
        List<NetconfTopologyRestconfClient.RemoteNode> nodes = client.parseTopology(json, "test-url");

        assertEquals(1, nodes.size());
        assertEquals("device-3", nodes.get(0).getNodeId());
        assertEquals("unable-to-connect", nodes.get(0).getConnectionStatus());
        assertTrue(nodes.get(0).getAvailableCapabilities().isEmpty());

        client.close();
    }

    @Test
    void testParseTopologyEmpty() {
        String json = """
            {
              "network-topology:topology": []
            }
            """;

        NetconfTopologyRestconfClient client = new NetconfTopologyRestconfClient();
        List<NetconfTopologyRestconfClient.RemoteNode> nodes = client.parseTopology(json, "test-url");

        assertTrue(nodes.isEmpty());
        client.close();
    }

    @Test
    void testParseTopologyNoNodeArray() {
        String json = """
            {
              "network-topology:topology": [
                {
                  "topology-id": "topology-netconf"
                }
              ]
            }
            """;

        NetconfTopologyRestconfClient client = new NetconfTopologyRestconfClient();
        List<NetconfTopologyRestconfClient.RemoteNode> nodes = client.parseTopology(json, "test-url");

        assertTrue(nodes.isEmpty());
        client.close();
    }

    @Test
    void testParseTopologyInvalidJson() {
        NetconfTopologyRestconfClient client = new NetconfTopologyRestconfClient();
        List<NetconfTopologyRestconfClient.RemoteNode> nodes = client.parseTopology("not valid json", "test-url");

        assertTrue(nodes.isEmpty());
        client.close();
    }

    @Test
    void testParseTopologyNodeWithoutNetconfNode() {
        String json = """
            {
              "network-topology:topology": [
                {
                  "topology-id": "topology-netconf",
                  "node": [
                    {
                      "node-id": "device-4"
                    }
                  ]
                }
              ]
            }
            """;

        NetconfTopologyRestconfClient client = new NetconfTopologyRestconfClient();
        List<NetconfTopologyRestconfClient.RemoteNode> nodes = client.parseTopology(json, "test-url");

        assertEquals(1, nodes.size());
        assertEquals("device-4", nodes.get(0).getNodeId());
        assertNull(nodes.get(0).getConnectionStatus());
        assertTrue(nodes.get(0).getAvailableCapabilities().isEmpty());

        client.close();
    }

    @Test
    void testRemoteNodeToString() {
        NetconfTopologyRestconfClient.RemoteNode node = new NetconfTopologyRestconfClient.RemoteNode();
        node.setNodeId("test-node");
        node.setConnectionStatus("connected");
        node.setAvailableCapabilities(List.of("cap1", "cap2"));
        String str = node.toString();
        assertTrue(str.contains("test-node"));
        assertTrue(str.contains("connected"));
        assertTrue(str.contains("2"));
    }

    @Test
    void testRemoteNodeDefaults() {
        NetconfTopologyRestconfClient.RemoteNode node = new NetconfTopologyRestconfClient.RemoteNode();
        assertNotNull(node.getAvailableCapabilities());
        assertTrue(node.getAvailableCapabilities().isEmpty());
        assertFalse(node.getAvailableCapabilities() == null);
    }
}
