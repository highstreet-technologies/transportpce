/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.topology;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Host;

class TopologyWriterTest {

    private static Host invokeBuildHost(String host) throws Exception {
        Method method = TopologyWriter.class.getDeclaredMethod("buildHost", String.class);
        method.setAccessible(true);
        return (Host) method.invoke(null, host);
    }

    /**
     * An IPv4 literal must be wrapped in an {@code Ipv4Address} union member.
     */
    @Test
    void testBuildHostIpv4() throws Exception {
        String ipv4 = "192.168.1.1";
        Host host = invokeBuildHost(ipv4);

        assertNotNull(host);
        assertNotNull(host.getIpAddress());
        assertNotNull(host.getIpAddress().getIpv4Address());
        assertNull(host.getIpAddress().getIpv6Address());
        assertEquals(ipv4, host.stringValue());
    }

    /**
     * An IPv6 literal must be wrapped in an {@code Ipv6Address} union member instead of throwing
     * an {@link IllegalArgumentException}.
     */
    @Test
    void testBuildHostIpv6() throws Exception {
        String ipv6 = "2001:db8::1";
        Host host = invokeBuildHost(ipv6);

        assertNotNull(host);
        assertNotNull(host.getIpAddress());
        assertNotNull(host.getIpAddress().getIpv6Address());
        assertNull(host.getIpAddress().getIpv4Address());
        assertEquals(ipv6, host.stringValue());
    }

    /**
     * A full (non-compressed) IPv6 literal must also be handled.
     */
    @Test
    void testBuildHostIpv6FullForm() throws Exception {
        String ipv6 = "fe80:0:0:0:0:0:0:1";
        Host host = invokeBuildHost(ipv6);

        assertNotNull(host);
        assertNotNull(host.getIpAddress().getIpv6Address());
        assertNull(host.getIpAddress().getIpv4Address());
    }

    /**
     * Building a host from an IPv6 literal must not throw, which is the regression scenario
     * previously causing an {@link IllegalArgumentException}.
     */
    @Test
    void testBuildHostIpv6DoesNotThrow() {
        assertDoesNotThrow(() -> invokeBuildHost("::1"));
    }
}
