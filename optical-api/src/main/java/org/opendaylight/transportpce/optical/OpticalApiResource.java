/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.optical.model.DataproviderInputWrapper;
import org.opendaylight.transportpce.optical.model.DataproviderOutput;
import org.opendaylight.transportpce.optical.model.DataproviderOutputPagination;
import org.opendaylight.transportpce.optical.model.DataproviderOutputWrapper;
import org.opendaylight.transportpce.optical.model.LinkMatchFilter;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev250905.Network;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev250905.mapping.Mapping;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev250905.network.Nodes;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev250905.network.NodesKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.NetworkId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.Networks;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.NetworkKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Network1;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.LinkKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Link1;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.opendaylight.yangtools.yang.common.Uint32;
import org.opendaylight.yangtools.yang.common.Uint64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JAX-RS resource exposing optical network data from the port-mapping datastore.
 *
 * <p>GET endpoints return RFC7951 JSON — the same wire format as
 * {@code /restconf/data/...} — so clients parse the objects identically. Each list entry (node, port mapping, link) is
 * returned as a bare RFC7951 object without the enclosing name/namespace wrapper.
 *
 * <p>The POST dataprovider endpoints keep their odlux dataprovider envelope
 * (wrapper / pagination) and carry RFC7951-serialized entries inside.
 */
@Path("/")
public class OpticalApiResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpticalApiResource.class);

    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER =
            new com.fasterxml.jackson.databind.ObjectMapper();

    private final DataBroker dataBroker;
    private final Rfc7951Serializer serializer;

    public OpticalApiResource(DataBroker dataBroker, BindingDOMCodecServices codecServices) {
        this.dataBroker = dataBroker;
        this.serializer = new Rfc7951Serializer(codecServices);
    }

    /**
     * All port-mapping nodes, each as a bare RFC7951 entry object wrapped in a JSON array.
     */
    @GET
    @Path("/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodes() {
        List<String> entries = new ArrayList<>();
        DataObjectIdentifier<Network> networkIID = DataObjectIdentifier.builder(Network.class).build();
        try (ReadTransaction readTx = dataBroker.newReadOnlyTransaction()) {
            Optional<Network> networkOpt = readTx.read(LogicalDatastoreType.CONFIGURATION, networkIID).get();
            if (networkOpt.isPresent()) {
                for (Nodes node : networkOpt.orElseThrow().nonnullNodes().values()) {
                    entries.add(serializeNode(node));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Failed to read port-mapping network data", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize port-mapping node", e);
        }
        return joinArray(entries);
    }

    /**
     * Single node as a bare RFC7951 entry object.
     */
    @GET
    @Path("/nodes/{nodeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNode(@PathParam("nodeId") String nodeId) {
        Nodes node = readNode(nodeId);
        if (node == null) {
            throw new NotFoundException("No port-mapping node with id '" + nodeId + "'");
        }
        try {
            return serializeNode(node);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize port-mapping node '" + nodeId + "'", e);
        }
    }

    /**
     * Port mappings of a node, each as a bare RFC7951 entry object wrapped in a JSON array.
     */
    @GET
    @Path("/nodes/{nodeId}/ports")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPorts(@PathParam("nodeId") String nodeId) {
        Nodes node = readNode(nodeId);
        if (node == null) {
            throw new NotFoundException("No port-mapping node with id '" + nodeId + "'");
        }
        List<String> entries = new ArrayList<>();
        try {
            for (Mapping mapping : node.nonnullMapping().values()) {
                entries.add(serializeMapping(node, mapping));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize port mapping of node '" + nodeId + "'", e);
        }
        return joinArray(entries);
    }

    @GET
    @Path("/links/uuids={uuids}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLinksByUuids(@PathParam("uuids") String uuidParam) {
        var uuids = List.of(uuidParam.split(","));
        List<String> entries = new ArrayList<>();
        try {
            for (Link link : this.getAllLinks()) {
                var link1 = link.augmentation(Link1.class);
                if (link1 != null && uuids.contains(link1.getUuid().getValue())) {
                    entries.add(serializeLink(link));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize link", e);
        }
        return joinArray(entries);
    }

    @POST
    @Path("/read-optical-links")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DataproviderOutputWrapper getLinks(DataproviderInputWrapper inputWrapper) throws IOException {
        var links = this.getAllLinks();
        var input = inputWrapper.getInput();
        final var pagination = input != null ? input.getPagination() : null;
        long pageSize = pagination != null ? pagination.getSize().longValue() : 20;
        long offset = pagination != null ? (pagination.getPage().longValue() - 1) * pageSize : 0;
        long page = offset / pageSize + 1;

        LOG.info("found {} links. filtering...", links.size());
        final var result = links.stream().filter(e -> LinkMatchFilter.matchFilter(e, input != null ? input.getFilter() : null))
                .sorted(new LinkDataProviderComparator<>(input != null ? input.getSortorder() : null))
                .toList();
        long total = result.size();
        LOG.info("found {} nodes for filter {}", total, input != null ? input.getFilter() : null);

        // serialize each link with the RFC7951 serializer (same wire format
        // as /restconf/data and the GET endpoints) and embed the resulting
        // JSON tree into the dataprovider envelope
        List<JsonNode> data = result.stream().skip(offset).limit(pageSize).map(e -> {
                    try {
                        return MAPPER.readTree(serializeLink(e));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .toList();

        return new DataproviderOutputWrapper(new DataproviderOutput(
                data,
                new DataproviderOutputPagination(Uint64.valueOf(page), Uint32.valueOf(pageSize),
                        Uint64.valueOf(total))));
    }


    private List<Link> getAllLinks() {
        var linkIID = DataObjectIdentifier.builder(Networks.class)
                .child(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.Network.class,
                        new NetworkKey(new NetworkId("openroadm-topology")))
                .augmentation(Network1.class)
                .build();

        try (ReadTransaction readTx = dataBroker.newReadOnlyTransaction()) {
            var result = readTx.read(LogicalDatastoreType.CONFIGURATION, linkIID).get().orElse(null);
            var linkList = result != null ? result.getLink() : null;
            return linkList != null ? new ArrayList<>(linkList.values()) : Collections.emptyList();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Failed to read links: ", e);
        }
    }

    private Nodes readNode(String nodeId) {
        DataObjectIdentifier.WithKey<Nodes, NodesKey> nodeIID =
                DataObjectIdentifier.builder(Network.class)
                        .child(Nodes.class, new NodesKey(nodeId))
                        .build();
        try (ReadTransaction readTx = dataBroker.newReadOnlyTransaction()) {
            return readTx.read(LogicalDatastoreType.CONFIGURATION, nodeIID).get().orElse(null);
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Failed to read port-mapping node '" + nodeId + "'", e);
        }
    }

    private String serializeNode(Nodes node) throws IOException {
        DataObjectIdentifier<Nodes> identifier = DataObjectIdentifier.builder(Network.class)
                .child(Nodes.class, new NodesKey(node.getNodeId()))
                .build();
        return Rfc7951Serializer.unwrapSingleElement(
                serializer.serializeDataObject(identifier, node));
    }

    private String serializeMapping(Nodes node, Mapping mapping) throws IOException {
        DataObjectIdentifier<Mapping> identifier = DataObjectIdentifier.builder(Network.class)
                .child(Nodes.class, new NodesKey(node.getNodeId()))
                .child(Mapping.class,
                        new org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev250905
                                .mapping.MappingKey(mapping.getLogicalConnectionPoint()))
                .build();
        return Rfc7951Serializer.unwrapSingleElement(
                serializer.serializeDataObject(identifier, mapping));
    }

    private String serializeLink(Link link) throws IOException {
        DataObjectIdentifier<Link> identifier = DataObjectIdentifier.builder(Networks.class)
                .child(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks
                        .Network.class, new NetworkKey(new NetworkId("openroadm-topology")))
                .augmentation(Network1.class)
                .child(Link.class, new LinkKey(link.getLinkId()))
                .build();
        return Rfc7951Serializer.unwrapSingleElement(
                serializer.serializeDataObject(identifier, link));
    }

    private static String joinArray(List<String> entries) {
        StringWriter writer = new StringWriter();
        writer.write("[");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                writer.write(",");
            }
            writer.write(entries.get(i));
        }
        writer.write("]");
        return writer.toString();
    }
}

