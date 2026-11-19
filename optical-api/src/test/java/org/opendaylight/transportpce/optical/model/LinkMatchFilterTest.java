/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev250110.Link1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev250110.Link1Builder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.state.types.rev191129.State;
import org.opendaylight.yang.gen.v1.http.org.openroadm.equipment.states.types.rev191129.AdminStates;
import org.opendaylight.yang.gen.v1.http.org.openroadm.network.types.rev250110.OpenroadmLinkType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.NodeId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.LinkId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.TpId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.LinkBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.LinkKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.Destination;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.DestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.Source;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.SourceBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Uuid;

/**
 * Reflection-free unit tests for {@link LinkMatchFilter} against the link
 * shapes of the germany-17 topology (ROADM-to-ROADM / ADD-LINK).
 */
class LinkMatchFilterTest {

    private static final String RTR_LINK_ID =
        "ROADM-Dortmund-DEG3-DEG3-TTP-TXRXtoROADM-Koeln-DEG1-DEG1-TTP-TXRX";
    private static final String ADD_LINK_ID =
        "ROADM-Nuernberg-SRG1-SRG1-CP-TXRXtoROADM-Nuernberg-DEG3-DEG3-CTP-TXRX";

    // --- sample links mirroring the germany-17 preload data ---

    private static Link roadmToRoadmLink() {
        Link1 commonNetwork = new Link1Builder()
                .setLinkType(OpenroadmLinkType.ROADMTOROADM)
                .setOperationalState(State.InService)
                .setAdministrativeState(AdminStates.InService)
                .setOppositeLink(new LinkId(
                    "ROADM-Koeln-DEG1-DEG1-TTP-TXRXtoROADM-Dortmund-DEG3-DEG3-TTP-TXRX"))
                .build();
        org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907
                .Link1Builder uuidAug = new org.opendaylight.yang.gen.v1.urn.opendaylight
                .transportpce.device.discovery.rev260907.Link1Builder()
                .setUuid(new Uuid("41ae27e5-7079-4885-9c35-8510b9845368"));
        return new LinkBuilder()
                .withKey(new LinkKey(new LinkId(RTR_LINK_ID)))
                .setLinkId(new LinkId(RTR_LINK_ID))
                .setSource(source("ROADM-Dortmund-DEG3", "DEG3-TTP-TXRX"))
                .setDestination(destination("ROADM-Koeln-DEG1", "DEG1-TTP-TXRX"))
                .addAugmentation(commonNetwork)
                .addAugmentation(uuidAug.build())
                .build();
    }

    private static Link addLink() {
        Link1 commonNetwork = new Link1Builder()
                .setLinkType(OpenroadmLinkType.ADDLINK)
                .setOperationalState(State.InService)
                .setAdministrativeState(AdminStates.InService)
                .setOppositeLink(new LinkId(
                    "ROADM-Nuernberg-DEG3-DEG3-CTP-TXRXtoROADM-Nuernberg-SRG1-SRG1-CP-TXRX"))
                .build();
        return new LinkBuilder()
                .withKey(new LinkKey(new LinkId(ADD_LINK_ID)))
                .setLinkId(new LinkId(ADD_LINK_ID))
                .setSource(source("ROADM-Nuernberg-SRG1", "SRG1-CP-TXRX"))
                .setDestination(destination("ROADM-Nuernberg-DEG3", "DEG3-CTP-TXRX"))
                .addAugmentation(commonNetwork)
                .build();
    }

    private static Source source(String node, String tp) {
        return new SourceBuilder()
                .setSourceNode(new NodeId(node))
                .setSourceTp(new TpId(tp))
                .build();
    }

    private static Destination destination(String node, String tp) {
        return new DestinationBuilder()
                .setDestNode(new NodeId(node))
                .setDestTp(new TpId(tp))
                .build();
    }

    private static DataproviderInputFilter filter(String property, String value) {
        DataproviderInputFilter f = new DataproviderInputFilter();
        f.setProperty(property);
        f.setFiltervalue(value);
        return f;
    }

    // --- tests ---

    @Test
    void nullOrEmptyFilterMatchesEverything() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(), null));
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(), List.of()));
    }

    @Test
    void linkIdFilterMatches() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(), List.of(filter("link-id", RTR_LINK_ID))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(), List.of(filter("link-id", ADD_LINK_ID))));
    }

    @Test
    void uuidFilterMatchesOnlyLinksWithUuidAugmentation() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("device-discovery:uuid", "41ae27e5-7079-4885-9c35-8510b9845368"))));
        // ADD-LINK carries no uuid augmentation -> must not match
        assertFalse(LinkMatchFilter.matchFilter(addLink(),
                List.of(filter("device-discovery:uuid", "41ae27e5-7079-4885-9c35-8510b9845368"))));
    }

    @Test
    void linkTypeFilterMatchesWireName() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("org-openroadm-common-network:link-type", "ROADM-TO-ROADM"))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("org-openroadm-common-network:link-type", "ADD-LINK"))));
        assertTrue(LinkMatchFilter.matchFilter(addLink(),
                List.of(filter("org-openroadm-common-network:link-type", "ADD-LINK"))));
        // short form without namespace must work as well
        assertTrue(LinkMatchFilter.matchFilter(addLink(), List.of(filter("link-type", "ADD-LINK"))));
    }

    @Test
    void operationalStateFilterMatches() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("org-openroadm-common-network:operational-state", "inService"))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("org-openroadm-common-network:operational-state", "outOfService"))));
    }

    @Test
    void administrativeStateFilterMatches() {
        assertTrue(LinkMatchFilter.matchFilter(addLink(),
                List.of(filter("org-openroadm-common-network:administrative-state", "inService"))));
        assertFalse(LinkMatchFilter.matchFilter(addLink(),
                List.of(filter("org-openroadm-common-network:administrative-state", "maintenance"))));
    }

    @Test
    void oppositeLinkFilterMatches() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("org-openroadm-common-network:opposite-link",
                    "ROADM-Koeln-DEG1-DEG1-TTP-TXRXtoROADM-Dortmund-DEG3-DEG3-TTP-TXRX"))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("org-openroadm-common-network:opposite-link", "some-other-link"))));
    }

    @Test
    void sourceFiltersMatch() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("source.source-node", "ROADM-Dortmund-DEG3"))));
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("source.source-tp", "DEG3-TTP-TXRX"))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("source.source-node", "ROADM-Koeln-DEG1"))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("source.source-tp", "DEG1-TTP-TXRX"))));
    }

    @Test
    void destinationFiltersMatch() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("destination.dest-node", "ROADM-Koeln-DEG1"))));
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("destination.dest-tp", "DEG1-TTP-TXRX"))));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("destination.dest-node", "ROADM-Dortmund-DEG3"))));
    }

    @Test
    void multipleFiltersAreCombinedWithAnd() {
        var filters = List.of(
                filter("org-openroadm-common-network:link-type", "ROADM-TO-ROADM"),
                filter("org-openroadm-common-network:operational-state", "inService"),
                filter("source.source-node", "ROADM-Dortmund-DEG3"));
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(), filters));
        // one non-matching criterion rejects the link
        var conflicting = List.of(
                filter("org-openroadm-common-network:link-type", "ROADM-TO-ROADM"),
                filter("org-openroadm-common-network:operational-state", "outOfService"));
        assertFalse(LinkMatchFilter.matchFilter(roadmToRoadmLink(), conflicting));
    }

    @Test
    void unknownPropertyIsIgnored() {
        assertTrue(LinkMatchFilter.matchFilter(roadmToRoadmLink(),
                List.of(filter("some.unknown:property", "whatever"))));
    }
}
