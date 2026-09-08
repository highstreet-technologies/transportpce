/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class SbRestconfConfigLoaderTest {

    @Test
    void testLoadFromInputStream() {
        String props = """
            controller.bearer.token = test-token
            controller.list = uuid1,uuid2
            controller.uuid1.baseurl = https://ctrl-1:8443/rests
            controller.uuid2.baseurl = https://ctrl-2:8443/rests
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertEquals("test-token", config.getBearerToken());
        assertNotNull(config.getControllers());
        assertEquals(2, config.getControllers().size());
        assertEquals("uuid1", config.getControllers().get(0).getUuid());
        assertEquals("https://ctrl-1:8443/rests", config.getControllers().get(0).getBaseUrl());
        assertEquals("uuid2", config.getControllers().get(1).getUuid());
        assertEquals("https://ctrl-2:8443/rests", config.getControllers().get(1).getBaseUrl());
    }

    @Test
    void testLoadWithCustomMountPrefix() {
        String props = """
            controller.bearer.token = token
            mount.prefix = /custom/mount/node=
            controller.list = uuid1
            controller.uuid1.baseurl = https://ctrl-1:8443/rests
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertEquals("/custom/mount/node=", config.getMountPrefix());
    }

    @Test
    void testLoadDefaultMountPrefix() {
        String props = """
            controller.bearer.token = token
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertEquals(SbRestconfConfig.DEFAULT_MOUNT_PREFIX, config.getMountPrefix());
    }

    @Test
    void testLoadWithEmptyControllerList() {
        String props = """
            controller.bearer.token = token
            controller.list =
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertTrue(config.getControllers().isEmpty());
    }

    @Test
    void testLoadWithControllerMissingBaseUrl() {
        String props = """
            controller.bearer.token = token
            controller.list = uuid1,uuid2
            controller.uuid1.baseurl = https://ctrl-1:8443/rests
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        // uuid2 has no baseurl — should be skipped
        assertEquals(1, config.getControllers().size());
        assertEquals("uuid1", config.getControllers().get(0).getUuid());
    }

    @Test
    void testLoadDefaults() {
        String props = """
            # empty
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertEquals("change-me", config.getBearerToken());
        assertEquals(SbRestconfConfig.DEFAULT_MOUNT_PREFIX, config.getMountPrefix());
        assertTrue(config.getControllers().isEmpty());
    }

    @Test
    void testLoadWithEnvVarSubstitution() {
        String props = """
            controller.bearer.token = ${env:SB_RESTCONF_TEST_TOKEN:-default-token}
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertEquals("default-token", config.getBearerToken());
    }

    @Test
    void testFindControllerAfterLoad() {
        String props = """
            controller.bearer.token = token
            controller.list = uuid1,uuid2
            controller.uuid1.baseurl = https://ctrl-1:8443/rests
            controller.uuid2.baseurl = https://ctrl-2:8443/rests
            """;
        InputStream is = new ByteArrayInputStream(props.getBytes(StandardCharsets.UTF_8));
        SbRestconfConfig config = SbRestconfConfigLoader.load(is);

        assertTrue(config.findController("uuid1").isPresent());
        assertEquals("https://ctrl-1:8443/rests", config.findController("uuid1").orElseThrow().getBaseUrl());
        assertTrue(config.findController("unknown").isEmpty());
    }
}
