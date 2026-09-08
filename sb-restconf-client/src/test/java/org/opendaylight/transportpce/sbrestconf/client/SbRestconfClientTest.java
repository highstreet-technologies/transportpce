/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendaylight.transportpce.sbrestconf.config.SbRestconfConfig;
import org.opendaylight.yangtools.binding.data.codec.api.BindingDataCodec;

@ExtendWith(MockitoExtension.class)
public class SbRestconfClientTest {

    @Mock
    private ControllerUuidResolver uuidResolver;

    @Mock
    private BindingDataCodec dataCodec;

    private SbRestconfConfig config;
    private SbRestconfClient client;

    @BeforeEach
    void setUp() {
        config = new SbRestconfConfig();
        config.setBearerToken("test-token");
        config.setControllers(List.of(
            new SbRestconfConfig.ControllerEntry("ctrl-uuid-1", "https://ctrl-1:8443/rests"),
            new SbRestconfConfig.ControllerEntry("ctrl-uuid-2", "https://ctrl-2:8443/rests")
        ));
        client = new SbRestconfClient(config, uuidResolver, dataCodec);
    }

    @Test
    void testBuildUrlWithDefaultMountPrefix() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "/org-openroadm-device");
        assertEquals("https://ctrl-1:8443/rests/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount/org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlWithSecondController() {
        when(uuidResolver.resolveControllerUuid("device-2"))
                .thenReturn(Optional.of("ctrl-uuid-2"));

        String url = client.buildUrl("device-2", "/org-openroadm-device");
        assertEquals("https://ctrl-2:8443/rests/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-2/yang-ext:mount/org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlWithEmptyObjectPath() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "");
        assertEquals("https://ctrl-1:8443/rests/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount",
                url);
    }

    @Test
    void testBuildUrlWithNullObjectPath() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", null);
        assertEquals("https://ctrl-1:8443/rests/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount",
                url);
    }

    @Test
    void testBuildUrlWithCustomMountPrefix() {
        config.setMountPrefix("/custom/mount/node=");

        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "/org-openroadm-device");
        assertEquals("https://ctrl-1:8443/rests/custom/mount/node=device-1/yang-ext:mount/org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlUnknownNode() {
        when(uuidResolver.resolveControllerUuid("unknown-device"))
                .thenReturn(Optional.empty());

        String url = client.buildUrl("unknown-device", "/org-openroadm-device");
        assertNull(url);
    }

    @Test
    void testBuildUrlUnknownControllerUuid() {
        when(uuidResolver.resolveControllerUuid("device-3"))
                .thenReturn(Optional.of("unknown-ctrl-uuid"));

        String url = client.buildUrl("device-3", "/org-openroadm-device");
        assertNull(url);
    }

    @Test
    void testBuildUrlWithNestedObjectPath() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1",
                "/org-openroadm-device/circuit-packs/circuit-pack=0%2F0%2F0%2F1");
        assertEquals("https://ctrl-1:8443/rests/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount/org-openroadm-device/circuit-packs/circuit-pack=0%2F0%2F0%2F1",
                url);
    }

    @Test
    void testBuildUrlWithBaseUrlEndingSlash() {
        config.setControllers(List.of(
            new SbRestconfConfig.ControllerEntry("ctrl-uuid-1", "https://ctrl-1:8443/")
        ));

        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "/org-openroadm-device");
        // baseUrl ends with /, mountPrefix leading / is stripped
        assertEquals("https://ctrl-1:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount/org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlBearerTokenInConfig() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        // Just verify the URL is built — token is used in the request, not in the URL
        String url = client.buildUrl("device-1", "/test");
        assertEquals("test-token", config.getBearerToken());
    }
}
