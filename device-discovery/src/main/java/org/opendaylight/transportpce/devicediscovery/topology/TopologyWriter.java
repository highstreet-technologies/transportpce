/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.topology;

import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.NetconfNodeAugment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.NetconfNodeAugmentBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.netconf.node.augment.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.netconf.node.augment.NetconfNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Node1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Node1Builder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes device nodes into the MDSAL operational datastore (netconf-topology).
 * <p>
 * Each node is created with a NetconfNodeAugment containing the connection status and a device-discovery augmentation
 * carrying the controller-uuid.
 */
public class TopologyWriter {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyWriter.class);

    private static final String TOPOLOGY_NETCONF = "topology-netconf";

    private final DataBroker dataBroker;

    public TopologyWriter(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    /**
     * Write or update a node in the netconf-topology operational store.
     *
     * @param nodeId          the device node ID (from VES changeIdentifier)
     * @param controllerUuid  the UUID of the controller that reported the device
     * @param connectionState the connection state string (connecting/connected/disconnected)
     */
    public void writeNode(String nodeId, String controllerUuid, String connectionState) {
        LOG.info("Writing node {} to netconf-topology: controller={}, state={}",
                nodeId, controllerUuid, connectionState);

        NodeKey nodeKey = new NodeKey(new org.opendaylight.yang.gen.v1.urn
                .tbd.params.xml.ns.yang.network.topology.rev131021.NodeId(nodeId));

        DataObjectIdentifier<Node> nodeIid = DataObjectIdentifier.builder(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(new TopologyId(TOPOLOGY_NETCONF)))
                .child(Node.class, nodeKey)
                .build();

        // Build the device-discovery augmentation with controller-uuid
        Node1 ddAugment = new Node1Builder()
                .setControllerUuid(controllerUuid)
                .build();

        // Build the netconf-node augment with connection status
        NetconfNode netconfNode = new NetconfNodeBuilder()
                .setConnectionStatus(mapConnectionState(connectionState))
                .build();
        NetconfNodeAugment netconfAugment = new NetconfNodeAugmentBuilder()
                .setNetconfNode(netconfNode)
                .build();

        // Build the topology node with both augmentations
        Node node = new NodeBuilder()
                .setNodeId(new org.opendaylight.yang.gen.v1.urn
                        .tbd.params.xml.ns.yang.network.topology.rev131021.NodeId(nodeId))
                .addAugmentation(ddAugment)
                .addAugmentation(netconfAugment)
                .build();

        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.merge(LogicalDatastoreType.OPERATIONAL, nodeIid, node);
        try {
            tx.commit().get();
            LOG.debug("Node {} successfully written to operational store", nodeId);
        } catch (Exception e) {
            LOG.error("Failed to write node {} to operational store", nodeId, e);
        }
    }

    /**
     * Remove a node from the netconf-topology operational store.
     *
     * @param nodeId the device node ID to remove
     */
    public void deleteNode(String nodeId) {
        LOG.info("Deleting node {} from netconf-topology", nodeId);

        NodeKey nodeKey = new NodeKey(new org.opendaylight.yang.gen.v1.urn
                .tbd.params.xml.ns.yang.network.topology.rev131021.NodeId(nodeId));

        DataObjectIdentifier<Node> nodeIid = DataObjectIdentifier.builder(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(new TopologyId(TOPOLOGY_NETCONF)))
                .child(Node.class, nodeKey)
                .build();

        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.delete(LogicalDatastoreType.OPERATIONAL, nodeIid);
        try {
            tx.commit().get();
            LOG.debug("Node {} successfully deleted from operational store", nodeId);
        } catch (Exception e) {
            LOG.error("Failed to delete node {} from operational store", nodeId, e);
        }
    }

    /**
     * Map VES newState string to MDSAL ConnectionStatus enum.
     *
     * @param newState the VES newState string
     * @return corresponding ConnectionStatus enum value, defaulting to CONNECTING
     */
    private static org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205
            .ConnectionOper.ConnectionStatus mapConnectionState(String newState) {
        if (newState == null) {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205
                    .ConnectionOper.ConnectionStatus.Connecting;
        }
        return switch (newState.toLowerCase()) {
            case "connected" -> org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205
                    .ConnectionOper.ConnectionStatus.Connected;
            case "connecting" -> org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205
                    .ConnectionOper.ConnectionStatus.Connecting;
            case "disconnected" -> org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205
                    .ConnectionOper.ConnectionStatus.UnableToConnect;
            default -> {
                LOG.warn("Unknown connection state '{}', defaulting to Connecting", newState);
                yield org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205
                        .ConnectionOper.ConnectionStatus.Connecting;
            }
        };
    }
}
