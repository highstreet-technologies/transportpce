/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads SbRestconfConfig from a Java properties file.
 *
 * Supports environment variable substitution via ${env:VAR:-default} syntax.
 *
 * Expected properties:
 *   controller.bearer.token = change-me
 *   controller.list = uuid1,uuid2
 *   controller.uuid1.baseurl = https://controller-1:8443/rests
 *   controller.uuid2.baseurl = https://controller-2:8443/rests
 *   # Optional: override the mount prefix (default is the standard ODL path)
 *   mount.prefix = /rests/data/network-topology:network-topology/topology=topology-netconf/node=
 */
public final class SbRestconfConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(SbRestconfConfigLoader.class);

    private static final String DEFAULT_CONFIG_PATH = "etc/org.opendaylight.transportpce.sbrestconf.cfg";

    private static final Pattern ENV_PATTERN = Pattern.compile(
            "\\$\\{env:([A-Za-z_][A-Za-z0-9_]*)(?::-(.*?))?\\}");

    private SbRestconfConfigLoader() {
    }

    public static SbRestconfConfig load() {
        return load(DEFAULT_CONFIG_PATH);
    }

    public static SbRestconfConfig load(String configPath) {
        Path path = Path.of(configPath);
        LOG.info("Loading sb-restconf config from: {}", path.toAbsolutePath());

        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(path)) {
            props.load(is);
        } catch (IOException e) {
            LOG.error("Failed to load config from {}, using defaults", configPath, e);
            return createDefaultConfig();
        }

        return parseConfig(props);
    }

    public static SbRestconfConfig load(InputStream inputStream) {
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            LOG.error("Failed to load config from input stream, using defaults", e);
            return createDefaultConfig();
        }
        return parseConfig(props);
    }

    private static SbRestconfConfig parseConfig(Properties props) {
        SbRestconfConfig config = new SbRestconfConfig();
        config.setBearerToken(resolveValue(props, "controller.bearer.token", "change-me"));
        config.setMountPrefix(resolveValue(props, "mount.prefix", SbRestconfConfig.DEFAULT_MOUNT_PREFIX));

        String controllerListStr = resolveValue(props, "controller.list", "");
        List<SbRestconfConfig.ControllerEntry> controllers = new ArrayList<>();
        if (!controllerListStr.isBlank()) {
            for (String uuid : controllerListStr.split(",")) {
                uuid = uuid.trim();
                if (uuid.isEmpty()) {
                    continue;
                }
                String baseUrlKey = "controller." + uuid + ".baseurl";
                String baseUrl = resolveValue(props, baseUrlKey, "");
                if (!baseUrl.isBlank()) {
                    controllers.add(new SbRestconfConfig.ControllerEntry(uuid, baseUrl));
                    LOG.info("Configured controller: uuid={}, baseUrl={}", uuid, baseUrl);
                } else {
                    LOG.warn("Controller {} has no baseurl configured, skipping", uuid);
                }
            }
        }
        config.setControllers(controllers);

        LOG.info("Sb-restconf config loaded: bearerToken=***, mountPrefix={}, controllers={}",
                config.getMountPrefix(), controllers.size());

        return config;
    }

    private static String resolveValue(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return resolveEnvVars(value);
    }

    private static String resolveEnvVars(String value) {
        Matcher matcher = ENV_PATTERN.matcher(value);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String envVar = matcher.group(1);
            String defaultVal = matcher.group(2) != null ? matcher.group(2) : "";
            String envValue = System.getenv(envVar);
            String replacement = envValue != null ? envValue : defaultVal;
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static SbRestconfConfig createDefaultConfig() {
        SbRestconfConfig config = new SbRestconfConfig();
        config.setBearerToken("change-me");
        config.setMountPrefix(SbRestconfConfig.DEFAULT_MOUNT_PREFIX);
        config.setControllers(List.of());
        LOG.warn("Using default sb-restconf config — no controllers configured");
        return config;
    }
}
