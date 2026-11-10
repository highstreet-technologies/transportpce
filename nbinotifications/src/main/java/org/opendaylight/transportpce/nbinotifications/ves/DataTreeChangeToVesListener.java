/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.ves;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.common.network.NetworkTransactionService;
import org.opendaylight.transportpce.nbinotifications.producer.Publisher;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev250110.Link1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.network.types.rev250110.OpenroadmLinkType;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.AToZDirection;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.AToZDirectionBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.ZToADirection;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.ZToADirectionBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.atoz.direction.AToZ;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.atoz.direction.AToZBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.atoz.direction.AToZKey;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.ztoa.direction.ZToA;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.ztoa.direction.ZToABuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.path.description.ztoa.direction.ZToAKey;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.pce.resource.Resource;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.pce.resource.ResourceBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.pce.resource.resource.resource.NodeBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.pce.resource.resource.resource.TerminationPoint;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription.rev230501.pce.resource.resource.resource.TerminationPointBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.service.types.rev220118.service.path.PathDescription;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.service.types.rev220118.service.path.PathDescriptionBuilder;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.servicepath.rev171017.service.path.list.ServicePaths;
import org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.servicepath.rev171017.service.path.list.ServicePathsBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.NetworkId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.Networks;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.NodeId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.Network;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.NetworkKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.network.Node;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.network.NodeKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.LinkBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.DestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.SourceBuilder;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic DataTreeChangeListener publishing MD-SAL data tree changes as ONAP VES notification events.
 *
 * <p>Instead of defining dedicated YANG notifications per event type, the
 * original data objects exactly as stored in MD-SAL are wrapped into {@link VesEvent} and forwarded to a Kafka
 * publisher using {@link VesEventSerializer}. Modification types are mapped to VES changeTypes as follows:
 *
 * <ul>
 *   <li>WRITE on a new object -&gt; created</li>
 *   <li>WRITE / SUBTREE_MODIFIED on an existing object -&gt; updated</li>
 *   <li>DELETE -&gt; deleted</li>
 * </ul>
 *
 * @param <T> type of the observed data object
 */
public class DataTreeChangeToVesListener<T extends DataObject> implements DataTreeChangeListener<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DataTreeChangeToVesListener.class);

    private final Publisher<VesEvent<? extends DataObject>> publisher;
    private final String topic;
    private final String sourceName;
    private final NetworkTransactionService dataBroker;

    /**
     * Listener publishing all observed changes of the observed data tree to the given topic.
     *
     * @param publisher  publisher to send the events to
     * @param topic      kafka topic
     * @param sourceName value of the VES commonEventHeader.sourceName
     */
    public DataTreeChangeToVesListener(Publisher<VesEvent<? extends DataObject>> publisher, String topic,
            String sourceName, NetworkTransactionService dataBroker) {
        this.publisher = publisher;
        this.topic = topic;
        this.sourceName = sourceName;
        this.dataBroker = dataBroker;
    }

    @Override
    public void onDataTreeChanged(@NonNull List<DataTreeModification<T>> changes) {
        for (DataTreeModification<T> change : changes) {
            DataObjectModification<T> rootNode = change.getRootNode();
            VesEvent<T> event = toEvent(change.path(), rootNode);
            if (event == null) {
                continue;
            }
            LOG.info("Publishing VES event '{}' for object '{}'", event.getChangeType(),
                    event.getDataObject().implementedInterface().getSimpleName());
            //publisher.sendEvent(event, topic);
            if (event.getDataObject() instanceof Link link) {
                var betterEvent = toLinkSimplified(link, event.getChangeType(), event.getIdentifier());
                if (betterEvent != null) {
                    publisher.sendEvent(link.getLinkId().getValue(), betterEvent, topic);
                }
            } else if (event.getDataObject() instanceof ServicePaths servicePaths) {
                var betterEvent = toServiceSimplified(servicePaths, event.getChangeType(), event.getIdentifier());
                if (betterEvent != null) {
                    publisher.sendEvent(servicePaths.getServicePathName(), betterEvent, topic);
                }
            } else {
                LOG.warn("unknown class {}. wont handle", event.getDataObject().getClass().getName());
            }
        }
    }

    /**
     * Map a service path to a simplified service path event.
     *
     * <p>The service-path-name and the path description are kept, but all
     * node references (node cases and tp-node-id of termination points) are
     * mapped from openroadm-topology node ids to the mounted openroadm-network
     * node ids, as expected by the unm-controller. Service end points are
     * dropped: they reference a different node-id domain and are not needed
     * for topology correlation.
     */
    private VesEvent<? extends DataObject> toServiceSimplified(ServicePaths servicePaths,
            VesEvent.ChangeType changeType, DataObjectIdentifier<T> identifier) {
        if (servicePaths.getServicePathName() == null) {
            LOG.debug("wont map service path without service-path-name");
            return null;
        }
        PathDescription pathDescription = servicePaths.getPathDescription();
        if (pathDescription == null) {
            LOG.debug("wont map service path without path-description");
            return null;
        }
        AToZDirection atozDirection = pathDescription.getAToZDirection();
        ZToADirection ztoaDirection = pathDescription.getZToADirection();
        if (atozDirection == null || ztoaDirection == null) {
            LOG.debug("wont map service path with incomplete path-description");
            return null;
        }
        // LinkedHashMap keeps the hop order of the original path, which is
        // the essential information of a service route. The generated key
        // classes do not implement Comparable, so a TreeMap cannot be used.
        // Hops are therefore sorted by their numeric key id (which is the
        // hop index assigned by the PCE) before insertion.
        Map<AToZKey, AToZ> atozMapped = new LinkedHashMap<>();
        atozDirection.nonnullAToZ().values().stream()
                .sorted(java.util.Comparator.comparingInt(hop -> parseHopIndex(hop.key().getId())))
                .forEach(atoz -> {
                    atozMapped.put(atoz.key(), atoz);
                });
        for (AToZ atoz : atozMapped.values()) {
            Resource mappedResource = mapResource(atoz.getResource());
            if (mappedResource == null) {
                return null;
            }
            atozMapped.put(atoz.key(), new AToZBuilder()
                    .withKey(atoz.key())
                    .setId(atoz.getId())
                    .setResource(mappedResource)
                    .build());
        }
        Map<ZToAKey, ZToA> ztoaMapped = new LinkedHashMap<>();
        ztoaDirection.nonnullZToA().values().stream()
                .sorted(java.util.Comparator.comparingInt(hop -> parseHopIndex(hop.key().getId())))
                .forEach(ztoa -> {
                    ztoaMapped.put(ztoa.key(), ztoa);
                });
        for (ZToA ztoa : ztoaMapped.values()) {
            Resource mappedResource = mapResource(ztoa.getResource());
            if (mappedResource == null) {
                return null;
            }
            ztoaMapped.put(ztoa.key(), new ZToABuilder()
                    .withKey(ztoa.key())
                    .setId(ztoa.getId())
                    .setResource(mappedResource)
                    .build());
        }
        var simplifiedPathDescription = new PathDescriptionBuilder()
                .setAToZDirection(new AToZDirectionBuilder()
                        .setAToZ(atozMapped)
                        .build())
                .setZToADirection(new ZToADirectionBuilder()
                        .setZToA(ztoaMapped)
                        .build())
                .build();
        return new VesEvent<>(changeType, identifier,
                (T) new ServicePathsBuilder()
                        .setServicePathName(servicePaths.getServicePathName())
                        .setPathDescription(simplifiedPathDescription)
                        .build(),
                sourceName);
    }

    /**
     * Parse the numeric hop index from a path hop key id. Falls back to
     * Integer.MAX_VALUE for non-numeric ids so they are kept at the end of
     * the ordered hop list.
     */
    private static int parseHopIndex(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Map the node references of a single path hop resource to mounted node
     * ids. Link resources are kept as-is, node and termination point
     * resources get their node ids resolved.
     */
    private Resource mapResource(Resource resource) {
        if (resource == null || resource.getResource() == null) {
            return null;
        }
        var choice = resource.getResource();
        if (choice
                instanceof org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription
                .rev230501.pce.resource.resource.resource.Link link) {
            return new ResourceBuilder()
                    .setResource(new org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface
                            .pathdescription.rev230501.pce.resource.resource.resource.LinkBuilder()
                            .setLinkId(link.getLinkId()).build())
                    .setState(resource.getState())
                    .build();
        }
        if (choice
                instanceof org.opendaylight.yang.gen.v1.http.org.transportpce.b.c._interface.pathdescription
                .rev230501.pce.resource.resource.resource.Node node) {
            var mappedNode = resolveNodeId(node.getNodeId());
            if (mappedNode.isEmpty()) {
                LOG.debug("unable to map path node {} to mounted node", node.getNodeId());
                return null;
            }
            return new ResourceBuilder()
                    .setResource(new NodeBuilder()
                            .setNodeId(mappedNode.orElseThrow())
                            .build())
                    .setState(resource.getState())
                    .build();
        }
        if (choice instanceof TerminationPoint tp) {
            var mappedTpNode = resolveNodeId(tp.getTpNodeId());
            if (mappedTpNode.isEmpty()) {
                LOG.debug("unable to map path tp node {} to mounted node", tp.getTpNodeId());
                return null;
            }
            return new ResourceBuilder()
                    .setResource(new TerminationPointBuilder()
                            .setTpId(tp.getTpId())
                            .setTpNodeId(mappedTpNode.orElseThrow())
                            .build())
                    .setState(resource.getState())
                    .build();
        }
        LOG.debug("wont map unknown path resource {}", choice.implementedInterface().getName());
        return null;
    }

    private VesEvent<? extends DataObject> toLinkSimplified(Link link, VesEvent.ChangeType changeType,
            DataObjectIdentifier<T> identifier) {
        var augLink = link.augmentation(Link1.class);
        if (augLink == null) {
            LOG.debug("wont map link which does not augment {}", Link1.class.getName());
            return null;
        }
        if (augLink.getLinkType() != OpenroadmLinkType.ROADMTOROADM || augLink.getLinkType() != OpenroadmLinkType.XPONDEROUTPUT
                || augLink.getLinkType() != OpenroadmLinkType.XPONDERINPUT) {
            LOG.debug("wont map link with type {}", augLink.getLinkType());
            return null;
        }
        var src = link != null ? link.getSource() : null;
        var srcNode = src != null ? src.getSourceNode().getValue() : null;
        var mappedSourceNode = this.resolveNodeId(srcNode);
        if (mappedSourceNode.isEmpty()) {
            LOG.debug("unable to map sourceNode {} to mounted node", srcNode);
            return null;
        }
        var dst = link != null ? link.getDestination() : null;
        var dstNode = dst != null ? dst.getDestNode().getValue() : null;
        var mappedDestNode = this.resolveNodeId(dstNode);
        if (mappedDestNode.isEmpty()) {
            LOG.debug("unable to map destNode {} to mounted node", srcNode);
            return null;
        }
        return new VesEvent<>(changeType, identifier,
                (T) new LinkBuilder()
                        .setLinkId(link.getLinkId())
                        .setSource(
                                new SourceBuilder().setSourceNode(new NodeId(mappedSourceNode.orElseThrow())).build())
                        .setDestination(
                                new DestinationBuilder().setDestNode(new NodeId(mappedDestNode.orElseThrow())).build())
                        .build(),
                sourceName);


    }

    private Optional<String> resolveNodeId(String nodeIdWithExt) {
        @NonNull DataObjectIdentifier<Node> nodeDoi =
                DataObjectIdentifier.builder(Networks.class)
                        .child(Network.class, new NetworkKey(new NetworkId("openroadm-topology")))
                        .child(Node.class, new NodeKey(new NodeId(nodeIdWithExt)))
                        .build();
        Optional<Node> onode;
        try {
            onode = this.dataBroker.read(LogicalDatastoreType.CONFIGURATION, nodeDoi).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("unable to read node {}: {}", nodeIdWithExt, e.getMessage());
            return Optional.empty();
        }
        if (onode.isEmpty()) {
            return Optional.empty();
        }
        var node = onode.orElseThrow();
        var supportingNodes = node.getSupportingNode();
        if (supportingNodes == null || supportingNodes.isEmpty()) {
            return Optional.empty();
        }
        var nodeRef = supportingNodes.entrySet().stream()
                .filter(e -> "openroadm-network".equals(e.getValue().getNetworkRef().getValue()))
                .map(e -> e.getValue().getNodeRef().getValue()).findFirst();
        return nodeRef;

    }

    private VesEvent<T> toEvent(DataObjectIdentifier<T> path, DataObjectModification<T> rootNode) {
        T dataBefore = rootNode.dataBefore();
        T dataAfter = rootNode.dataAfter();
        VesEvent.ChangeType changeType;
        T dataObject;
        switch (rootNode.modificationType()) {
            case WRITE:
                if (dataBefore == null) {
                    changeType = VesEvent.ChangeType.CREATED;
                    dataObject = dataAfter;
                } else {
                    changeType = VesEvent.ChangeType.UPDATED;
                    dataObject = dataAfter;
                }
                break;
            case SUBTREE_MODIFIED:
                changeType = VesEvent.ChangeType.UPDATED;
                dataObject = dataAfter;
                break;
            case DELETE:
                changeType = VesEvent.ChangeType.DELETED;
                dataObject = dataBefore;
                break;
            default:
                LOG.debug("Ignoring modification type {}", rootNode.modificationType());
                return null;
        }
        if (dataObject == null) {
            LOG.warn("Data object of modification is null, skipping event");
            return null;
        }
        return new VesEvent<>(changeType, path, dataObject, sourceName);
    }
}
