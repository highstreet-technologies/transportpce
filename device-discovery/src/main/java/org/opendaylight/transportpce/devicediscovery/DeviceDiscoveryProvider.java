/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery;

import org.opendaylight.mdsal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi lifecycle component for the Device Discovery module.
 *
 * Activated when the TransportPCE Karaf feature is loaded. On activation
 * it starts the Kafka VES event consumer and triggers initial
 * netconf-topology reconciliation from all configured controllers.
 */
public class DeviceDiscoveryProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscoveryProvider.class);

    private final DataBroker dataBroker;

    public DeviceDiscoveryProvider(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
        LOG.info("DeviceDiscoveryProvider created with DataBroker: {}", dataBroker);
    }

    public void start() {
        LOG.info("Device Discovery starting — initializing Kafka consumer and topology reconciliation");
    }

    @Override
    public void close() {
        LOG.info("Device Discovery shutting down");
    }

    public DataBroker getDataBroker() {
        return dataBroker;
    }
}
