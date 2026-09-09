/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.topology;

import java.util.ArrayList;
import java.util.List;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.devicediscovery.reconciliation.NetconfTopologyRestconfClient.RemoteNode;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.base._1._0.rev110601.SessionIdType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Host;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.ConnectionOper.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.AvailableCapabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.AvailableCapabilitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.UnavailableCapabilities;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.UnavailableCapabilitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.available.capabilities.AvailableCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.available.capabilities.AvailableCapability.CapabilityOrigin;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.available.capabilities.AvailableCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.unavailable.capabilities.UnavailableCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.unavailable.capabilities.UnavailableCapability.FailureReason;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.device.rev251205.connection.oper.unavailable.capabilities.UnavailableCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.NetconfNodeAugment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.NetconfNodeAugmentBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.netconf.node.augment.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.netconf.node.augment.NetconfNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Node1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Node1Builder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.yang.common.Uint16;
import org.opendaylight.yangtools.yang.common.Uint32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes device nodes into the MDSAL operational datastore (netconf-topology).
 *
 * Each node is created with the full information from the remote controller:
 * connection status, host, port, session-id, available/unavailable capabilities,
 * and the device-discovery augmentation carrying the controller-uuid.
 */
public class TopologyWriter {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyWriter.class);
    private static final String TOPOLOGY_NETCONF = "topology-netconf";

    private final DataBroker dataBroker;

    public TopologyWriter(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    /**
     * Write or update a node in the netconf-topology operational store with full data.
     *
     * @param remoteNode the complete node data from the controller
     * @param controllerUuid the UUID of the controller that reported the device
     */
    public void writeNode(RemoteNode remoteNode, String controllerUuid) {
        String nodeId = remoteNode.getNodeId();
        LOG.info("Writing node {} to netconf-topology: controller={}, state={}",
                nodeId, controllerUuid, remoteNode.getConnectionStatus());

        NodeKey nodeKey = new NodeKey(new NodeId(nodeId));

        DataObjectIdentifier<Node> nodeIid = DataObjectIdentifier.builder(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(new TopologyId(TOPOLOGY_NETCONF)))
                .child(Node.class, nodeKey)
                .build();

        // Build the device-discovery augmentation with controller-uuid
        Node1 ddAugment = new Node1Builder()
                .setControllerUuid(controllerUuid)
                .build();

        // Build available capabilities
        List<AvailableCapability> availCapList = new ArrayList<>();
        for (String cap : remoteNode.getAvailableCapabilities()) {
            availCapList.add(new AvailableCapabilityBuilder()
                    .setCapability(cap)
                    .setCapabilityOrigin(CapabilityOrigin.DeviceAdvertised)
                    .build());
        }
        AvailableCapabilities availableCapabilities = new AvailableCapabilitiesBuilder()
                .setAvailableCapability(availCapList)
                .build();

        // Build unavailable capabilities
        List<UnavailableCapability> unavailCapList = new ArrayList<>();
        for (String cap : remoteNode.getUnavailableCapabilities()) {
            unavailCapList.add(new UnavailableCapabilityBuilder()
                    .setCapability(cap)
                    .setFailureReason(FailureReason.MissingSource)
                    .build());
        }
        UnavailableCapabilities unavailableCapabilities = new UnavailableCapabilitiesBuilder()
                .setUnavailableCapability(unavailCapList)
                .build();

        // Build the netconf-node augment with full data
        NetconfNodeBuilder netconfNodeBuilder = new NetconfNodeBuilder()
                .setConnectionStatus(mapConnectionState(remoteNode.getConnectionStatus()))
                .setAvailableCapabilities(availableCapabilities)
                .setUnavailableCapabilities(unavailableCapabilities);

        if (remoteNode.getHost() != null && !remoteNode.getHost().isBlank()) {
            netconfNodeBuilder.setHost(new Host(new IpAddress(new Ipv4Address(remoteNode.getHost()))));
        }
        if (remoteNode.getPort() > 0) {
            netconfNodeBuilder.setPort(new PortNumber(Uint16.valueOf(remoteNode.getPort())));
        }
        if (remoteNode.getSessionId() > 0) {
            netconfNodeBuilder.setSessionId(new SessionIdType(Uint32.valueOf(remoteNode.getSessionId())));
        }
        if (remoteNode.getConnectedMessage() != null) {
            netconfNodeBuilder.setConnectedMessage(remoteNode.getConnectedMessage());
        }

        NetconfNode netconfNode = netconfNodeBuilder.build();
        NetconfNodeAugment netconfAugment = new NetconfNodeAugmentBuilder()
                .setNetconfNode(netconfNode)
                .build();

        // Build the topology node with both augmentations
        Node node = new NodeBuilder()
                .setNodeId(new NodeId(nodeId))
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

        NodeKey nodeKey = new NodeKey(new NodeId(nodeId));

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
     * Map connection-status string from RESTCONF to MDSAL ConnectionStatus enum.
     */
    private static ConnectionStatus mapConnectionState(String state) {
        if (state == null) {
            return ConnectionStatus.Connecting;
        }
        return switch (state.toLowerCase()) {
            case "connected" -> ConnectionStatus.Connected;
            case "connecting" -> ConnectionStatus.Connecting;
            case "unable-to-connect" -> ConnectionStatus.UnableToConnect;
            default -> {
                LOG.warn("Unknown connection state '{}', defaulting to Connecting", state);
                yield ConnectionStatus.Connecting;
            }
        };
    }
}
