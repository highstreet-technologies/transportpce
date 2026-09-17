/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.config;

import java.util.List;
import java.util.Optional;

/**
 * Unified configuration for TransportPCE, read from a single properties file
 * (e.g. {@code etc/org.opendaylight.transportpce.cfg}).
 *
 * <p>Contains:
 * <ul>
 *   <li>Kafka settings for the VES event consumer (device-discovery)</li>
 *   <li>Controller list (UUID → base URL) shared by device-discovery and sb-restconf-client</li>
 *   <li>Shared bearer token for RESTCONF authentication</li>
 *   <li>Optional mount prefix for device-level RESTCONF access (sb-restconf-client)</li>
 * </ul>
 *
 * <p>Expected properties in the config file:
 * <pre>
 *   kafka.bootstrap.servers = kafka:9092
 *   kafka.topic = unauthenticated.VES_NOTIFICATION_OUTPUT
 *   kafka.group.id = transportpce-device-discovery
 *   controller.bearer.token = {shared-bearer-token}
 *   controller.list = uuid1,uuid2,uuid3
 *   controller.uuid1.baseurl = https://controller-1:8443/rests
 *   controller.uuid2.baseurl = https://controller-2:8443/rests
 *   # Optional: override the mount prefix (default is the standard ODL netconf-topology mount path)
 *   # mount.prefix = /rests/data/network-topology:network-topology/topology=topology-netconf/node=
 * </pre>
 */
public class TransportPceConfig {

    /**
     * Default RESTCONF mount path prefix for device-level access.
     * The full URL is: {controller-base-url}{MOUNT_PREFIX}{node-id}{mount-suffix}{object-path}
     */
    public static final String DEFAULT_MOUNT_PREFIX =
            "/data/network-topology:network-topology/topology=topology-netconf/node=";

    private String kafkaBootstrapServers;
    private String kafkaTopic;
    private String kafkaGroupId;
    private String bearerToken;
    private String mountPrefix = DEFAULT_MOUNT_PREFIX;
    private List<ControllerEntry> controllers;

    private String gnpyUrl;
    private String gnpyUsername;

    public void setGnpyUrl(String gnpyUrl) {
        this.gnpyUrl = gnpyUrl;
    }

    public void setGnpyUsername(String gnpyUsername) {
        this.gnpyUsername = gnpyUsername;
    }

    public void setGnpyPassword(String gnpyPassword) {
        this.gnpyPassword = gnpyPassword;
    }

    private String gnpyPassword;

    public boolean isGnpyEnabled(){
        return this.gnpyUrl!=null && !this.gnpyUrl.isBlank();
    }
    public boolean isIetfNetworkTopology() {
        return this.mountPrefix.contains("ietf-network:networks");
    }

    public boolean isNetconfTopology() {
        return this.mountPrefix.contains("topology=topology-netconf");
    }

    public static class ControllerEntry {
        private final String uuid;
        private final String baseUrl;

        public ControllerEntry(String uuid, String baseUrl) {
            this.uuid = uuid;
            this.baseUrl = baseUrl;
        }

        public String getUuid() {
            return uuid;
        }

        public String getBaseUrl() {
            return baseUrl;
        }
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getKafkaGroupId() {
        return kafkaGroupId;
    }

    public void setKafkaGroupId(String kafkaGroupId) {
        this.kafkaGroupId = kafkaGroupId;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getMountPrefix() {
        return mountPrefix;
    }

    public void setMountPrefix(String mountPrefix) {
        this.mountPrefix = mountPrefix;
    }

    public List<ControllerEntry> getControllers() {
        return controllers;
    }

    public void setControllers(List<ControllerEntry> controllers) {
        this.controllers = controllers;
    }

    public String getGnpyPassword() {
        return gnpyPassword;
    }

    public String getGnpyUsername() {
        return gnpyUsername;
    }

    public String getGnpyUrl() {
        return gnpyUrl;
    }
    /**
     * Find the controller base URL for a given reportingEntityId (UUID).
     *
     * @param uuid the reportingEntityId from the VES commonEventHeader
     * @return the matching controller entry, or empty if not found
     */
    public Optional<ControllerEntry> findController(String uuid) {
        if (controllers == null || uuid == null) {
            return Optional.empty();
        }
        return controllers.stream()
                .filter(c -> c.getUuid().equals(uuid))
                .findFirst();
    }
}
