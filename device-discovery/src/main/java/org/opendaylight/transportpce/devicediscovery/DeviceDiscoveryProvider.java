/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery;

import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.transportpce.devicediscovery.config.ConfigLoader;
import org.opendaylight.transportpce.devicediscovery.config.DeviceDiscoveryConfig;
import org.opendaylight.transportpce.devicediscovery.kafka.VesKafkaConsumer;
import org.opendaylight.transportpce.devicediscovery.reconciliation.NetconfTopologyRestconfClient;
import org.opendaylight.transportpce.devicediscovery.reconciliation.TopologyReconciliationService;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lifecycle component for the Device Discovery module.
 *
 * On startup it loads configuration from a properties file, performs initial
 * netconf-topology reconciliation from all configured controllers, and starts
 * the Kafka VES event consumer for real-time device lifecycle events.
 *
 * Works both in OSGi/Karaf (via blueprint) and Lighty (via direct instantiation).
 */
public class DeviceDiscoveryProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscoveryProvider.class);
    private static final String DEFAULT_CONFIG_PATH = "etc/org.opendaylight.transportpce.devicediscovery.cfg";

    private final DataBroker dataBroker;
    private final String configPath;
    private DeviceDiscoveryConfig config;
    private TopologyWriter topologyWriter;
    private VesKafkaConsumer kafkaConsumer;
    private NetconfTopologyRestconfClient restconfClient;
    private TopologyReconciliationService reconciliationService;

    /**
     * Constructor for OSGi/Karaf — uses default config path.
     *
     * @param dataBroker MDSAL DataBroker
     */
    public DeviceDiscoveryProvider(DataBroker dataBroker) {
        this(dataBroker, DEFAULT_CONFIG_PATH);
    }

    /**
     * Constructor with explicit config path — for Lighty and tests.
     *
     * @param dataBroker MDSAL DataBroker
     * @param configPath path to the properties config file
     */
    public DeviceDiscoveryProvider(DataBroker dataBroker, String configPath) {
        this.dataBroker = dataBroker;
        this.configPath = configPath;
        LOG.info("DeviceDiscoveryProvider created with DataBroker: {}, configPath: {}", dataBroker, configPath);
    }

    /**
     * Called on startup (by OSGi blueprint or Lighty module).
     *
     * Loads config from properties file, runs reconciliation, starts Kafka consumer.
     */
    public void start() {
        LOG.info("Device Discovery starting");

        // Load configuration from properties file
        config = ConfigLoader.load(configPath);

        topologyWriter = new TopologyWriter(dataBroker);

        // Initial reconciliation: fetch netconf-topology from all configured controllers
        restconfClient = new NetconfTopologyRestconfClient();
        reconciliationService = new TopologyReconciliationService(config, topologyWriter, restconfClient);
        try {
            reconciliationService.reconcile();
        } catch (Exception e) {
            LOG.error("Topology reconciliation failed, continuing with Kafka consumer startup", e);
        }
        restconfClient.close();

        // Start Kafka consumer for real-time VES events
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
        if (restconfClient != null) {
            restconfClient.close();
        }
    }

    public DataBroker getDataBroker() {
        return dataBroker;
    }

    public DeviceDiscoveryConfig getConfig() {
        return config;
    }
}
