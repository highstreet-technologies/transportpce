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
 * Configuration for the Device Discovery module.
 *
 * Read from a config file (e.g. etc/org.opendaylight.transportpce.devicediscovery.cfg)
 * containing:
 *
 *   kafka.bootstrap.servers = kafka:9092
 *   kafka.topic = unmNotifications
 *   kafka.group.id = transportpce-device-discovery
 *   controller.bearer.token = {shared-bearer-token}
 *   controller.list = uuid1,uuid2,uuid3
 *   controller.uuid1.baseurl = https://controller-1:8443/rests
 *   controller.uuid2.baseurl = https://controller-2:8443/rests
 *   controller.uuid3.baseurl = https://controller-3:8443/rests
 */
public class DeviceDiscoveryConfig {

    private String kafkaBootstrapServers;
    private String kafkaTopic;
    private String kafkaGroupId;
    private String bearerToken;
    private List<ControllerEntry> controllers;

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

    public List<ControllerEntry> getControllers() {
        return controllers;
    }

    public void setControllers(List<ControllerEntry> controllers) {
        this.controllers = controllers;
    }

    /**
     * Find the controller base URL for a given reportingEntityId (UUID).
     *
     * @param uuid the reportingEntityId from the VES commonEventHeader
     * @return the base URL of the matching controller, or empty if not found
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
