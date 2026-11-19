/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opendaylight.transportpce.optical.model.DataproviderInputSortOrder;
import org.opendaylight.transportpce.optical.model.DataproviderInputSortOrder.SortOrder;
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

/**
 * Unit tests for {@link LinkDataProviderComparator} using link shapes from
 * the germany-17 topology.
 */
class LinkDataProviderComparatorTest {

    private static Link link(String linkId, String linkType, String sourceNode, String destNode,
            String oppositeLink) {
        Link1Builder commonNetwork = new Link1Builder()
                .setLinkType(OpenroadmLinkType.valueOf(linkType))
                .setOperationalState(State.InService)
                .setAdministrativeState(AdminStates.InService);
        if (oppositeLink != null) {
            commonNetwork.setOppositeLink(new LinkId(oppositeLink));
        }
        return new LinkBuilder()
                .withKey(new LinkKey(new LinkId(linkId)))
                .setLinkId(new LinkId(linkId))
                .setSource(new SourceBuilder()
                        .setSourceNode(new NodeId(sourceNode))
                        .setSourceTp(new TpId("tp"))
                        .build())
                .setDestination(new DestinationBuilder()
                        .setDestNode(new NodeId(destNode))
                        .setDestTp(new TpId("tp"))
                        .build())
                .addAugmentation(commonNetwork.build())
                .build();
    }

    private static DataproviderInputSortOrder sort(String property, SortOrder order) {
        return new DataproviderInputSortOrder(property, order);
    }

    @Test
    void nullOrEmptySortOrdersCompareEqual() {
        Link a = link("a", "ADDLINK", "n1", "n2", null);
        assertEquals(0, new LinkDataProviderComparator(null).compare(a, a));
        assertEquals(0, new LinkDataProviderComparator(List.of()).compare(a, a));
    }

    @Test
    void linkIdSortsAscendingAndDescending() {
        Link dortmund = link("ROADM-Dortmund-...", "ADDLINK", "n1", "n2", null);
        Link koeln = link("ROADM-Koeln-...", "ADDLINK", "n1", "n2", null);

        var asc = new LinkDataProviderComparator(List.of(sort("link-id", SortOrder.ascending)));
        assertTrue(asc.compare(dortmund, koeln) < 0, "Dortmund sorts before Koeln ascending");
        assertTrue(asc.compare(koeln, dortmund) > 0);

        var desc = new LinkDataProviderComparator(List.of(sort("link-id", SortOrder.descending)));
        assertTrue(desc.compare(dortmund, koeln) > 0, "Dortmund sorts after Koeln descending");
        assertTrue(desc.compare(koeln, dortmund) < 0);
    }

    @Test
    void linkTypeSortsByWireName() {
        Link addLink = link("a", "ADDLINK", "n1", "n2", null);
        Link roadmLink = link("b", "ROADMTOROADM", "n1", "n2", null);

        var byLinkType = new LinkDataProviderComparator(
                List.of(sort("org-openroadm-common-network:link-type", SortOrder.ascending)));
        // ADD-LINK < ROADM-TO-ROADM lexicographically
        assertTrue(byLinkType.compare(addLink, roadmLink) < 0);
        assertTrue(byLinkType.compare(roadmLink, addLink) > 0);

        // short property form must work as well
        var shortForm = new LinkDataProviderComparator(List.of(sort("link-type", SortOrder.ascending)));
        assertTrue(shortForm.compare(addLink, roadmLink) < 0);
    }

    @Test
    void sourceNodeSorts() {
        Link dortmund = link("a", "ADDLINK", "ROADM-Dortmund-DEG3", "n2", null);
        Link koeln = link("b", "ADDLINK", "ROADM-Koeln-DEG1", "n2", null);

        var bySource = new LinkDataProviderComparator(
                List.of(sort("source.source-node", SortOrder.ascending)));
        assertTrue(bySource.compare(dortmund, koeln) < 0);

        var bySourceDesc = new LinkDataProviderComparator(
                List.of(sort("source.source-node", SortOrder.descending)));
        assertTrue(bySourceDesc.compare(dortmund, koeln) > 0);
    }

    @Test
    void destinationNodeSorts() {
        Link toKoeln = link("a", "ADDLINK", "n1", "ROADM-Koeln-DEG1", null);
        Link toMuenchen = link("b", "ADDLINK", "n1", "ROADM-Muenchen-DEG1", null);

        var byDest = new LinkDataProviderComparator(
                List.of(sort("destination.dest-node", SortOrder.ascending)));
        assertTrue(byDest.compare(toKoeln, toMuenchen) < 0);
    }

    @Test
    void oppositeLinkSorts() {
        Link withOpposite = link("a", "ADDLINK", "n1", "n2", "opposite-zzz");
        Link withoutOpposite = link("b", "ADDLINK", "n1", "n2", null);

        var byOpposite = new LinkDataProviderComparator(
                List.of(sort("org-openroadm-common-network:opposite-link", SortOrder.ascending)));
        // null (missing opposite-link) sorts last
        assertTrue(byOpposite.compare(withOpposite, withoutOpposite) < 0);
        assertTrue(byOpposite.compare(withoutOpposite, withOpposite) > 0);
    }

    @Test
    void multipleSortCriteriaAppliedInOrder() {
        // same link-type, different link-id -> second criterion decides
        Link a = link("ROADM-A-...", "ADDLINK", "n1", "n2", null);
        Link b = link("ROADM-B-...", "ADDLINK", "n1", "n2", null);

        var byTypeThenId = new LinkDataProviderComparator(List.of(
                sort("org-openroadm-common-network:link-type", SortOrder.ascending),
                sort("link-id", SortOrder.ascending)));
        assertTrue(byTypeThenId.compare(a, b) < 0);
    }

    @Test
    void missingSortValueSortsLast() {
        // ROADM type vs missing link-type augmentation
        Link withType = link("b", "ROADMTOROADM", "n1", "n2", null);
        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.LinkBuilder linkWithoutAugBuilder = new org.opendaylight.yang
                .gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks
                .network.LinkBuilder()
                .withKey(new LinkKey(new LinkId("a")))
                .setLinkId(new LinkId("a"))
                .setSource(new SourceBuilder().setSourceNode(new NodeId("n1")).setSourceTp(new TpId("tp")).build())
                .setDestination(new DestinationBuilder().setDestNode(new NodeId("n2")).setDestTp(new TpId("tp")).build());
        Link withoutType = linkWithoutAugBuilder.build();

        var byType = new LinkDataProviderComparator(
                List.of(sort("org-openroadm-common-network:link-type", SortOrder.ascending)));
        assertTrue(byType.compare(withType, withoutType) < 0, "missing link-type sorts last");
        assertTrue(byType.compare(withoutType, withType) > 0);
    }

    @Test
    void unknownSortPropertyIsIgnored() {
        Link a = link("a", "ADDLINK", "n1", "n2", null);
        Link b = link("zzz", "ADDLINK", "n1", "n2", null);

        var byUnknown = new LinkDataProviderComparator(
                List.of(sort("some.unknown:property", SortOrder.ascending)));
        assertEquals(0, byUnknown.compare(a, b), "unknown property must not influence order");
    }
}
