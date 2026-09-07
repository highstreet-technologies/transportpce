/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ConfigLoaderTest {

    @Test
    void testLoadFromInputStream() {
        String props = """
                kafka.bootstrap.servers = kafka:9092
                kafka.topic = test-notifications
                kafka.group.id = test-group
                controller.bearer.token = test-token
                controller.list = uuid1,uuid2
                controller.uuid1.baseurl = https://ctrl-1:8443/rests
                controller.uuid2.baseurl = https://ctrl-2:8443/rests
                """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        DeviceDiscoveryConfig config = ConfigLoader.load(is);

        assertEquals("kafka:9092", config.getKafkaBootstrapServers());
        assertEquals("test-notifications", config.getKafkaTopic());
        assertEquals("test-group", config.getKafkaGroupId());
        assertEquals("test-token", config.getBearerToken());
        assertNotNull(config.getControllers());
        assertEquals(2, config.getControllers().size());
        assertEquals("uuid1", config.getControllers().get(0).getUuid());
        assertEquals("https://ctrl-1:8443/rests", config.getControllers().get(0).getBaseUrl());
        assertEquals("uuid2", config.getControllers().get(1).getUuid());
        assertEquals("https://ctrl-2:8443/rests", config.getControllers().get(1).getBaseUrl());
    }

    @Test
    void testFindController() {
        String props = """
                kafka.bootstrap.servers = kafka:9092
                controller.bearer.token = token
                controller.list = uuid1,uuid2
                controller.uuid1.baseurl = https://ctrl-1:8443/rests
                controller.uuid2.baseurl = https://ctrl-2:8443/rests
                """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        DeviceDiscoveryConfig config = ConfigLoader.load(is);

        assertTrue(config.findController("uuid1").isPresent());
        assertEquals("https://ctrl-1:8443/rests", config.findController("uuid1").orElseThrow().getBaseUrl());
        assertTrue(config.findController("uuid2").isPresent());
        assertTrue(config.findController("unknown").isEmpty());
    }

    @Test
    void testLoadWithEmptyControllerList() {
        String props = """
                kafka.bootstrap.servers = kafka:9092
                controller.bearer.token = token
                controller.list =
                """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        DeviceDiscoveryConfig config = ConfigLoader.load(is);

        assertNotNull(config.getControllers());
        assertTrue(config.getControllers().isEmpty());
    }

    @Test
    void testLoadWithControllerMissingBaseUrl() {
        String props = """
                kafka.bootstrap.servers = kafka:9092
                controller.bearer.token = token
                controller.list = uuid1,uuid2
                controller.uuid1.baseurl = https://ctrl-1:8443/rests
                """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        DeviceDiscoveryConfig config = ConfigLoader.load(is);

        // uuid2 has no baseurl — should be skipped
        assertEquals(1, config.getControllers().size());
        assertEquals("uuid1", config.getControllers().get(0).getUuid());
    }

    @Test
    void testLoadWithDefaultValues() {
        String props = """
                # empty config
                """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        DeviceDiscoveryConfig config = ConfigLoader.load(is);

        assertEquals("localhost:9092", config.getKafkaBootstrapServers());
        assertEquals("unmNotifications", config.getKafkaTopic());
        assertEquals("transportpce-device-discovery", config.getKafkaGroupId());
        assertEquals("change-me", config.getBearerToken());
        assertTrue(config.getControllers().isEmpty());
    }

    @Test
    void testLoadWithEnvVarSubstitution() {
        // Set env var for test
        // Note: System.getenv is not easily mockable, so we test the default value path
        String props = """
                kafka.bootstrap.servers = ${env:KAFKA_TEST_BOOTSTRAP:-fallback:9092}
                controller.bearer.token = ${env:CONTROLLER_TEST_TOKEN:-default-token}
                """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        DeviceDiscoveryConfig config = ConfigLoader.load(is);

        // Env vars are not set, so defaults should be used
        assertEquals("fallback:9092", config.getKafkaBootstrapServers());
        assertEquals("default-token", config.getBearerToken());
    }
}
