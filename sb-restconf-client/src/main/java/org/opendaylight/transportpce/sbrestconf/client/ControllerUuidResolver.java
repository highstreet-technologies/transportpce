/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.client;

import java.util.Optional;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev251205.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Node1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves the controller-uuid for a given device node-id by reading
 * the device-discovery augmentation from MDSAL operational datastore.
 *
 * The controller-uuid was written by the device-discovery module when
 * the device was registered via a VES lifecycle event.
 */
public class ControllerUuidResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerUuidResolver.class);

    private static final String TOPOLOGY_NETCONF = "topology-netconf";

    private final DataBroker dataBroker;

    public ControllerUuidResolver(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    /**
     * Read the controller-uuid for a given node-id from the operational datastore.
     *
     * @param nodeId the device node-id
     * @return the controller-uuid, or empty if the node is not found or has no augmentation
     */
    public Optional<String> resolveControllerUuid(String nodeId) {
        LOG.debug("Resolving controller-uuid for node {}", nodeId);

        DataObjectIdentifier<Node> nodeIid = DataObjectIdentifier.builder(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(new TopologyId(TOPOLOGY_NETCONF)))
                .child(Node.class, new NodeKey(new NodeId(nodeId)))
                .build();

        try (ReadTransaction tx = dataBroker.newReadOnlyTransaction()) {
            Optional<Node> nodeOpt = tx.read(LogicalDatastoreType.OPERATIONAL, nodeIid).get();
            if (nodeOpt.isEmpty()) {
                LOG.warn("Node {} not found in operational datastore", nodeId);
                return Optional.empty();
            }

            Node node = nodeOpt.orElseThrow();
            Node1 ddAugment = node.augmentation(Node1.class);
            if (ddAugment == null || ddAugment.getControllerUuid() == null) {
                LOG.warn("Node {} has no controller-uuid augmentation", nodeId);
                return Optional.empty();
            }

            String controllerUuid = ddAugment.getControllerUuid();
            LOG.debug("Resolved controller-uuid for node {}: {}", nodeId, controllerUuid);
            return Optional.of(controllerUuid);

        } catch (Exception e) {
            LOG.error("Failed to read controller-uuid for node {}", nodeId, e);
            return Optional.empty();
        }
    }
}
