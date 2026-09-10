/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.config;

import java.util.List;
import java.util.Optional;

/**
 * Configuration for the southbound RESTCONF client.
 *
 * Contains the controller list (UUID → base URL) and the shared bearer token.
 * The base URL prefix for device-level RESTCONF is configurable but defaults
 * to the standard ODL netconf-topology mount path.
 */
public class SbRestconfConfig {

    /**
     * Default RESTCONF mount path prefix for device-level access.
     * The full URL is: {controller-base-url}{MOUNT_PREFIX}{node-id}{object-path}
     */
    public static final String DEFAULT_MOUNT_PREFIX =
            "/data/network-topology:network-topology/topology=topology-netconf/node=";

    private String bearerToken;
    private String mountPrefix = DEFAULT_MOUNT_PREFIX;
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

    /**
     * Find the controller base URL for a given controller UUID.
     *
     * @param uuid the controller UUID
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
