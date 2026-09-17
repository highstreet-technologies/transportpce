/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.config;

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
 * Loads {@link TransportPceConfig} from a Java properties file.
 *
 * <p>Supports environment variable substitution via {@code ${env:VAR:-default}} syntax.
 *
 * <p>Expected properties:
 * <pre>
 *   kafka.bootstrap.servers = localhost:9092
 *   kafka.topic = unmNotifications
 *   kafka.group.id = transportpce-device-discovery
 *   controller.bearer.token = change-me
 *   controller.list = uuid1,uuid2
 *   controller.uuid1.baseurl = https://controller-1:8443/rests
 *   controller.uuid2.baseurl = https://controller-2:8443/rests
 *   # Optional: override the mount prefix (default is the standard ODL netconf-topology mount path)
 *   # mount.prefix = /rests/data/network-topology:network-topology/topology=topology-netconf/node=
 * </pre>
 */
public final class TransportPceConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TransportPceConfigLoader.class);

    private static final String DEFAULT_CONFIG_PATH = "etc/org.opendaylight.transportpce.cfg";

    private static final Pattern ENV_PATTERN = Pattern.compile(
            "\\$\\{env:([A-Za-z_][A-Za-z0-9_]*)(?::-(.*?))?\\}");

    private TransportPceConfigLoader() {
    }

    /**
     * Load TransportPceConfig from the default config file path.
     *
     * @return populated TransportPceConfig
     */
    public static TransportPceConfig load() {
        return load(DEFAULT_CONFIG_PATH);
    }

    /**
     * Load TransportPceConfig from a specific properties file.
     *
     * @param configPath path to the properties file
     * @return populated TransportPceConfig
     */
    public static TransportPceConfig load(String configPath) {
        Path path = Path.of(configPath);
        LOG.info("Loading TransportPCE config from: {}", path.toAbsolutePath());

        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(path)) {
            props.load(is);
        } catch (IOException e) {
            LOG.error("Failed to load config from {}, using defaults", configPath, e);
            return createDefaultConfig();
        }

        return parseConfig(props);
    }

    /**
     * Load TransportPceConfig from properties loaded via an InputStream.
     *
     * @param inputStream input stream to a properties file
     * @return populated TransportPceConfig
     */
    public static TransportPceConfig load(InputStream inputStream) {
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            LOG.error("Failed to load config from input stream, using defaults", e);
            return createDefaultConfig();
        }
        return parseConfig(props);
    }

    private static TransportPceConfig parseConfig(Properties props) {
        TransportPceConfig config = new TransportPceConfig();
        config.setKafkaBootstrapServers(resolveValue(props, "kafka.bootstrap.servers", "localhost:9092"));
        config.setKafkaTopic(resolveValue(props, "kafka.topic", "unmNotifications"));
        config.setKafkaGroupId(resolveValue(props, "kafka.group.id", "transportpce-device-discovery"));
        config.setBearerToken(resolveValue(props, "controller.bearer.token", "change-me"));
        config.setMountPrefix(resolveValue(props, "mount.prefix", TransportPceConfig.DEFAULT_MOUNT_PREFIX));
        config.setGnpyUrl(resolveValue(props, "gnpy.url", null));
        config.setGnpyUsername(resolveValue(props, "gnpy.username", null));
        config.setGnpyPassword(resolveValue(props, "gnpy.password", null));

        // Parse controller list
        String controllerListStr = resolveValue(props, "controller.list", "");
        List<TransportPceConfig.ControllerEntry> controllers = new ArrayList<>();
        if (!controllerListStr.isBlank()) {
            for (String uuid : controllerListStr.split(",")) {
                uuid = uuid.trim();
                if (uuid.isEmpty()) {
                    continue;
                }
                String baseUrlKey = "controller." + uuid + ".baseurl";
                String baseUrl = resolveValue(props, baseUrlKey, "");
                if (!baseUrl.isBlank()) {
                    controllers.add(new TransportPceConfig.ControllerEntry(uuid, baseUrl));
                    LOG.info("Configured controller: uuid={}, baseUrl={}", uuid, baseUrl);
                } else {
                    LOG.warn("Controller {} has no baseurl configured, skipping", uuid);
                }
            }
        }
        config.setControllers(controllers);

        LOG.info("TransportPCE config loaded: kafka={}, topic={}, groupId={}, mountPrefix={}, controllers={}",
                config.getKafkaBootstrapServers(), config.getKafkaTopic(), config.getKafkaGroupId(),
                config.getMountPrefix(), controllers.size());

        return config;
    }

    /**
     * Resolve a property value, supporting ${env:VAR:-default} syntax.
     *
     * @param props the properties
     * @param key the property key
     * @param defaultValue the default value if key is not found
     * @return the resolved value
     */
    private static String resolveValue(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return resolveEnvVars(value);
    }

    /**
     * Resolve ${env:VAR:-default} patterns in a value string.
     *
     * @param value the raw value potentially containing env references
     * @return the value with env vars resolved
     */
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

    private static TransportPceConfig createDefaultConfig() {
        TransportPceConfig config = new TransportPceConfig();
        config.setKafkaBootstrapServers("localhost:9092");
        config.setKafkaTopic("unmNotifications");
        config.setKafkaGroupId("transportpce-device-discovery");
        config.setBearerToken("change-me");
        config.setMountPrefix(TransportPceConfig.DEFAULT_MOUNT_PREFIX);
        config.setControllers(List.of());
        LOG.warn("Using default config — no controllers configured, Kafka consumer will be inactive");
        return config;
    }
}
