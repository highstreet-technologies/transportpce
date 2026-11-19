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
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.node.types.rev210528.NodeIdType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.ConnectionType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.service.ServiceAEndBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.service.ServiceZEndBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.state.types.rev191129.LifecycleState;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.state.types.rev191129.State;
import org.opendaylight.yang.gen.v1.http.org.openroadm.equipment.states.types.rev191129.AdminStates;
import org.opendaylight.yang.gen.v1.http.org.openroadm.service.rev250110.service.list.Services;
import org.opendaylight.yang.gen.v1.http.org.openroadm.service.rev250110.service.list.ServicesBuilder;

/**
 * Unit tests for {@link ServiceDataProviderComparator}.
 */
class ServiceDataProviderComparatorTest {

    private static Services service(String name, String aNode, String zNode, LifecycleState lifecycle) {
        return new ServicesBuilder()
                .setServiceName(name)
                .setCommonId("common-" + name)
                .setConnectionType(ConnectionType.Service)
                .setLifecycleState(lifecycle)
                .setAdministrativeState(AdminStates.InService)
                .setOperationalState(State.InService)
                .setServiceAEnd(new ServiceAEndBuilder()
                        .setNodeId(new NodeIdType(aNode))
                        .setClli("CLLI-A")
                        .build())
                .setServiceZEnd(new ServiceZEndBuilder()
                        .setNodeId(new NodeIdType(zNode))
                        .setClli("CLLI-Z")
                        .build())
                .build();
    }

    private static DataproviderInputSortOrder sort(String property, SortOrder order) {
        return new DataproviderInputSortOrder(property, order);
    }

    @Test
    void nullOrEmptySortOrdersCompareEqual() {
        Services a = service("a", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        assertEquals(0, new ServiceDataProviderComparator(null).compare(a, a));
        assertEquals(0, new ServiceDataProviderComparator(List.of()).compare(a, a));
    }

    @Test
    void serviceNameSortsAscendingAndDescending() {
        Services alpha = service("service-alpha", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        Services beta = service("service-beta", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);

        var asc = new ServiceDataProviderComparator(List.of(sort("service-name", SortOrder.ascending)));
        assertTrue(asc.compare(alpha, beta) < 0);
        assertTrue(asc.compare(beta, alpha) > 0);

        var desc = new ServiceDataProviderComparator(List.of(sort("service-name", SortOrder.descending)));
        assertTrue(desc.compare(alpha, beta) > 0);
    }

    @Test
    void commonIdSorts() {
        Services a = service("a", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        Services b = service("b", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);

        var byCommonId = new ServiceDataProviderComparator(
                List.of(sort("common-id", SortOrder.ascending)));
        // common-a < common-b
        assertTrue(byCommonId.compare(a, b) < 0);
    }

    @Test
    void lifecycleStateSortsByWireName() {
        Services deployed = service("a", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        Services planned = service("b", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Planned);

        var byLifecycle = new ServiceDataProviderComparator(
                List.of(sort("lifecycle-state", SortOrder.ascending)));
        assertTrue(byLifecycle.compare(deployed, planned) < 0, "deployed < planned alphabetically");
    }

    @Test
    void endPointNodesSort() {
        Services ulm = service("a", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        Services berlin = service("b", "XPDR-BerlHamb", "XPDR-EsseDues", LifecycleState.Deployed);

        var byANode = new ServiceDataProviderComparator(
                List.of(sort("service-a-end.node-id", SortOrder.ascending)));
        assertTrue(byANode.compare(berlin, ulm) < 0);

        var byZNode = new ServiceDataProviderComparator(
                List.of(sort("service-z-end.node-id", SortOrder.ascending)));
        assertEquals(0, byZNode.compare(berlin, ulm), "same z-end must compare equal");
    }

    @Test
    void multipleCriteriaAppliedInOrder() {
        Services a = service("service-a", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        Services b = service("service-b", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);

        var byTypeThenName = new ServiceDataProviderComparator(List.of(
                sort("connection-type", SortOrder.ascending),
                sort("service-name", SortOrder.ascending)));
        assertTrue(byTypeThenName.compare(a, b) < 0);
    }

    @Test
    void missingLifecycleStateSortsLast() {
        Services withLifecycle = service("b", "XPDR-UlmMuen", "XPDR-EsseDues" , LifecycleState.Deployed);
        Services withoutLifecycle = new ServicesBuilder()
                .setServiceName("a")
                .setConnectionType(ConnectionType.Service)
                .setServiceAEnd(new ServiceAEndBuilder()
                        .setNodeId(new NodeIdType("XPDR-UlmMuen")).setClli("CLLI-A").build())
                .setServiceZEnd(new ServiceZEndBuilder()
                        .setNodeId(new NodeIdType("XPDR-EsseDues")).setClli("CLLI-Z").build())
                .build();

        var byLifecycle = new ServiceDataProviderComparator(
                List.of(sort("lifecycle-state", SortOrder.ascending)));
        assertTrue(byLifecycle.compare(withLifecycle, withoutLifecycle) < 0,
                "missing lifecycle-state sorts last");
        assertTrue(byLifecycle.compare(withoutLifecycle, withLifecycle) > 0);
    }

    @Test
    void unknownSortPropertyIsIgnored() {
        Services a = service("a", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);
        Services z = service("zzz", "XPDR-UlmMuen", "XPDR-EsseDues", LifecycleState.Deployed);

        var byUnknown = new ServiceDataProviderComparator(
                List.of(sort("some.unknown:property", SortOrder.ascending)));
        assertEquals(0, byUnknown.compare(a, z), "unknown property must not influence order");
    }
}
