/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

public class DeviceDiscoveryConfigTest {

    @Test
    void testFindControllerPresent() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setControllers(List.of(
                new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests"),
                new DeviceDiscoveryConfig.ControllerEntry("uuid-2", "https://ctrl-2:8443/rests")
        ));

        assertTrue(config.findController("uuid-1").isPresent());
        assertEquals("https://ctrl-1:8443/rests", config.findController("uuid-1").orElseThrow().getBaseUrl());
        assertTrue(config.findController("uuid-2").isPresent());
        assertEquals("https://ctrl-2:8443/rests", config.findController("uuid-2").orElseThrow().getBaseUrl());
    }

    @Test
    void testFindControllerAbsent() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setControllers(List.of(
                new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")
        ));

        assertTrue(config.findController("unknown-uuid").isEmpty());
    }

    @Test
    void testFindControllerWithNullControllers() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setControllers(null);

        assertTrue(config.findController("any-uuid").isEmpty());
    }

    @Test
    void testFindControllerWithNullUuid() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setControllers(List.of(
                new DeviceDiscoveryConfig.ControllerEntry("uuid-1", "https://ctrl-1:8443/rests")
        ));

        assertTrue(config.findController(null).isEmpty());
    }

    @Test
    void testControllerEntryGetters() {
        DeviceDiscoveryConfig.ControllerEntry entry =
                new DeviceDiscoveryConfig.ControllerEntry("my-uuid", "https://my-url:8443/rests");
        assertEquals("my-uuid", entry.getUuid());
        assertEquals("https://my-url:8443/rests", entry.getBaseUrl());
    }

    @Test
    void testConfigGettersSetters() {
        DeviceDiscoveryConfig config = new DeviceDiscoveryConfig();
        config.setKafkaBootstrapServers("kafka:9092");
        config.setKafkaTopic("my-topic");
        config.setKafkaGroupId("my-group");
        config.setBearerToken("my-token");
        config.setControllers(List.of());

        assertEquals("kafka:9092", config.getKafkaBootstrapServers());
        assertEquals("my-topic", config.getKafkaTopic());
        assertEquals("my-group", config.getKafkaGroupId());
        assertEquals("my-token", config.getBearerToken());
        assertFalse(config.getControllers() == null);
        assertTrue(config.getControllers().isEmpty());
    }
}
