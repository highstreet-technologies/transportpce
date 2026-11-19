/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.node.types.rev210528.NodeIdType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.ConnectionType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.service.ServiceAEnd;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.service.ServiceAEndBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.service.ServiceZEnd;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.service.types.rev250110.service.ServiceZEndBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.state.types.rev191129.LifecycleState;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.state.types.rev191129.State;
import org.opendaylight.yang.gen.v1.http.org.openroadm.equipment.states.types.rev191129.AdminStates;
import org.opendaylight.yang.gen.v1.http.org.openroadm.service.rev250110.service.list.Services;
import org.opendaylight.yang.gen.v1.http.org.openroadm.service.rev250110.service.list.ServicesBuilder;

/**
 * Unit tests for {@link ServiceMatchFilter} and ServiceDataProviderComparator
 * (via {@link org.opendaylight.transportpce.optical.ServiceDataProviderComparator}).
 */
class ServiceMatchFilterTest {

    private static Services service(String name, String commonId, String aNode, String zNode) {
        ServiceAEnd aEnd = new ServiceAEndBuilder()
                .setNodeId(new NodeIdType(aNode))
                .setClli("CLLI-A")
                .build();
        ServiceZEnd zEnd = new ServiceZEndBuilder()
                .setNodeId(new NodeIdType(zNode))
                .setClli("CLLI-Z")
                .build();
        return new ServicesBuilder()
                .setServiceName(name)
                .setCommonId(commonId)
                .setConnectionType(ConnectionType.Service)
                .setLifecycleState(LifecycleState.Deployed)
                .setAdministrativeState(AdminStates.InService)
                .setOperationalState(State.InService)
                .setServiceAEnd(aEnd)
                .setServiceZEnd(zEnd)
                .build();
    }

    private static DataproviderInputFilter filter(String property, String value) {
        DataproviderInputFilter f = new DataproviderInputFilter();
        f.setProperty(property);
        f.setFiltervalue(value);
        return f;
    }

    // --- filter tests ---

    @Test
    void nullOrEmptyFilterMatchesEverything() {
        assertTrue(ServiceMatchFilter.matchFilter(service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues"), null));
        assertTrue(ServiceMatchFilter.matchFilter(service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues"), List.of()));
    }

    @Test
    void serviceNameFilterMatches() {
        var svc = service("service1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc, List.of(filter("service-name", "service1"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc, List.of(filter("service-name", "other"))));
    }

    @Test
    void commonIdFilterMatches() {
        var svc = service("s1", "ASATT1234567", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc, List.of(filter("common-id", "ASATT1234567"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc, List.of(filter("common-id", "nope"))));
    }

    @Test
    void connectionTypeFilterMatchesWireName() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc, List.of(filter("connection-type", "service"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc, List.of(filter("connection-type", "infrastructure"))));
    }

    @Test
    void lifecycleStateFilterMatches() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc, List.of(filter("lifecycle-state", "deployed"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc, List.of(filter("lifecycle-state", "planned"))));
    }

    @Test
    void administrativeStateFilterMatches() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("administrative-state", "inService"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("administrative-state", "maintenance"))));
    }

    @Test
    void operationalStateFilterMatches() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("operational-state", "inService"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("operational-state", "outOfService"))));
    }

    @Test
    void endPointFiltersMatch() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("service-a-end.node-id", "XPDR-UlmMuen"))));
        assertTrue(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("service-z-end.node-id", "XPDR-EsseDues"))));
        assertTrue(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("service-a-end.clli", "CLLI-A"))));
        assertFalse(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("service-a-end.node-id", "XPDR-EsseDues"))));
    }

    @Test
    void multipleFiltersCombineWithAnd() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        var filters = List.of(
                filter("connection-type", "service"),
                filter("lifecycle-state", "deployed"));
        assertTrue(ServiceMatchFilter.matchFilter(svc, filters));
        var conflicting = List.of(
                filter("connection-type", "service"),
                filter("lifecycle-state", "planned"));
        assertFalse(ServiceMatchFilter.matchFilter(svc, conflicting));
    }

    @Test
    void unknownFilterPropertyIsIgnored() {
        var svc = service("s1", "c1", "XPDR-UlmMuen", "XPDR-EsseDues");
        assertTrue(ServiceMatchFilter.matchFilter(svc,
                List.of(filter("some.unknown:property", "whatever"))));
    }

}
