/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opendaylight.transportpce.devicediscovery.config.TransportPceConfig;

public class SbRestconfConfigTest {

    @Test
    void testFindControllerPresent() {
        TransportPceConfig config = new TransportPceConfig();
        config.setControllers(List.of(
            new TransportPceConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests"),
            new TransportPceConfig.ControllerEntry("uuid-2", "https://ctrl-2:8443/rests")
        ));

        assertTrue(config.findController("uuid-1").isPresent());
        assertEquals("https://ctrl-1:8443/rests", config.findController("uuid-1").orElseThrow().getBaseUrl());
        assertTrue(config.findController("uuid-2").isPresent());
    }

    @Test
    void testFindControllerAbsent() {
        TransportPceConfig config = new TransportPceConfig();
        config.setControllers(List.of(
            new TransportPceConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")
        ));

        assertTrue(config.findController("unknown").isEmpty());
    }

    @Test
    void testFindControllerNullControllers() {
        TransportPceConfig config = new TransportPceConfig();
        config.setControllers(null);
        assertTrue(config.findController("any").isEmpty());
    }

    @Test
    void testFindControllerNullUuid() {
        TransportPceConfig config = new TransportPceConfig();
        config.setControllers(List.of(
            new TransportPceConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")
        ));
        assertTrue(config.findController(null).isEmpty());
    }

    @Test
    void testDefaultMountPrefix() {
        TransportPceConfig config = new TransportPceConfig();
        assertEquals(TransportPceConfig.DEFAULT_MOUNT_PREFIX, config.getMountPrefix());
    }

    @Test
    void testCustomMountPrefix() {
        TransportPceConfig config = new TransportPceConfig();
        config.setMountPrefix("/custom/mount/path/node=");
        assertEquals("/custom/mount/path/node=", config.getMountPrefix());
    }

    @Test
    void testControllerEntryGetters() {
        TransportPceConfig.ControllerEntry entry =
                new TransportPceConfig.ControllerEntry("my-uuid", "https://my-url:8443/rests");
        assertEquals("my-uuid", entry.getUuid());
        assertEquals("https://my-url:8443/rests", entry.getBaseUrl());
    }

    @Test
    void testConfigGettersSetters() {
        TransportPceConfig config = new TransportPceConfig();
        config.setBearerToken("my-token");
        config.setMountPrefix("/test/mount=");
        config.setControllers(List.of());

        assertEquals("my-token", config.getBearerToken());
        assertEquals("/test/mount=", config.getMountPrefix());
        assertTrue(config.getControllers().isEmpty());
    }
}
