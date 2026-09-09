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
 * netconf-topology reconciliation from all configured controllers (fetching
 * full node data), and starts the Kafka VES event consumer for real-time
 * device lifecycle events (which also fetches full node data on connect).
 *
 * The RESTCONF client stays open for the lifetime of the provider because
 * the Kafka consumer uses it to fetch node data on "connected" events.
 */
public class DeviceDiscoveryProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDiscoveryProvider.class);
    private static final String DEFAULT_CONFIG_PATH = "etc/org.opendaylight.transportpce.cfg";

    private final DataBroker dataBroker;
    private final String configPath;
    private DeviceDiscoveryConfig config;
    private TopologyWriter topologyWriter;
    private VesKafkaConsumer kafkaConsumer;
    private NetconfTopologyRestconfClient restconfClient;

    public DeviceDiscoveryProvider(DataBroker dataBroker) {
        this(dataBroker, DEFAULT_CONFIG_PATH);
    }

    public DeviceDiscoveryProvider(DataBroker dataBroker, String configPath) {
        this.dataBroker = dataBroker;
        this.configPath = configPath;
        LOG.info("DeviceDiscoveryProvider created with DataBroker: {}, configPath: {}", dataBroker, configPath);
    }

    public void start() {
        LOG.info("Device Discovery starting");

        config = ConfigLoader.load(configPath);
        topologyWriter = new TopologyWriter(dataBroker);

        // RESTCONF client — stays open for Kafka consumer to use on "connected" events
        restconfClient = new NetconfTopologyRestconfClient();

        // Initial reconciliation: fetch full netconf-topology from all configured controllers
        TopologyReconciliationService reconciliationService =
                new TopologyReconciliationService(config, topologyWriter, restconfClient);
        try {
            reconciliationService.reconcile();
        } catch (Exception e) {
            LOG.error("Topology reconciliation failed, continuing with Kafka consumer startup", e);
        }

        // Start Kafka consumer (uses restconfClient for on-demand node fetches)
        kafkaConsumer = new VesKafkaConsumer(config, topologyWriter, restconfClient);
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
