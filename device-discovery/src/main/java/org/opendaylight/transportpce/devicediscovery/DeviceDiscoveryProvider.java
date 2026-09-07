/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery;

import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.transportpce.devicediscovery.config.DeviceDiscoveryConfig;
import org.opendaylight.transportpce.devicediscovery.kafka.VesKafkaConsumer;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;
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
    private DeviceDiscoveryConfig config;
    private TopologyWriter topologyWriter;
    private VesKafkaConsumer kafkaConsumer;

    public DeviceDiscoveryProvider(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
        LOG.info("DeviceDiscoveryProvider created with DataBroker: {}", dataBroker);
    }

    /**
     * Called by OSGi blueprint on startup.
     *
     * TODO: Load DeviceDiscoveryConfig from OSGi config file (etc/org.opendaylight.transportpce.devicediscovery.cfg).
     * For now the config must be set via setConfig() before calling start().
     */
    public void start() {
        LOG.info("Device Discovery starting — initializing Kafka consumer and topology reconciliation");

        if (config == null) {
            LOG.warn("DeviceDiscoveryConfig is null, Kafka consumer will not start. "
                    + "Set config via setConfig() before calling start().");
            return;
        }

        topologyWriter = new TopologyWriter(dataBroker);
        kafkaConsumer = new VesKafkaConsumer(config, topologyWriter);
        kafkaConsumer.start();

        LOG.info("Device Discovery started successfully");
    }

    @Override
    public void close() {
        LOG.info("Device Discovery shutting down");
        if (kafkaConsumer != null) {
            kafkaConsumer.stop();
        }
    }

    public DataBroker getDataBroker() {
        return dataBroker;
    }

    public void setConfig(DeviceDiscoveryConfig config) {
        this.config = config;
    }

    public DeviceDiscoveryConfig getConfig() {
        return config;
    }
}
