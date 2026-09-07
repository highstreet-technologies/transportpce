/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opendaylight.transportpce.devicediscovery.config.DeviceDiscoveryConfig;
import org.opendaylight.transportpce.devicediscovery.model.ves.Event;
import org.opendaylight.transportpce.devicediscovery.ves.VesEventWrapper;
import org.opendaylight.transportpce.devicediscovery.topology.TopologyWriter;
import org.opendaylight.transportpce.devicediscovery.ves.VesEventWrapperDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kafka consumer that listens for VES notification events on the configured topic.
 *
 * On each notification event it extracts the controller UUID (reportingEntityId),
 * the device node ID (changeIdentifier), and the lifecycle state (newState),
 * then delegates to TopologyWriter to update the MDSAL operational store.
 */
public class VesKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(VesKafkaConsumer.class);

    private final DeviceDiscoveryConfig config;
    private final TopologyWriter topologyWriter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private KafkaConsumer<String, VesEventWrapper> consumer;

    public VesKafkaConsumer(DeviceDiscoveryConfig config, TopologyWriter topologyWriter) {
        this.config = config;
        this.topologyWriter = topologyWriter;
    }

    public void start() {
        if (running.get()) {
            LOG.warn("Kafka consumer already running");
            return;
        }

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getKafkaGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, VesEventWrapperDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(config.getKafkaTopic()));

        running.set(true);
        executor.submit(this::pollLoop);
        LOG.info("Kafka consumer started — topic={}, groupId={}, bootstrap={}",
                config.getKafkaTopic(), config.getKafkaGroupId(), config.getKafkaBootstrapServers());
    }

    private void pollLoop() {
        while (running.get()) {
            try {
                var records = consumer.poll(Duration.ofMillis(1000));
                for (var record : records) {
                    processRecord(record.key(), record.value());
                }
            } catch (Exception e) {
                if (running.get()) {
                    LOG.error("Error in Kafka poll loop", e);
                }
            }
        }
        if (consumer != null) {
            consumer.close();
        }
        LOG.info("Kafka consumer stopped");
    }

    private void processRecord(String key, VesEventWrapper value) {
        try {
            Event event = value.getEvent();
            if (event == null) {
                LOG.debug("VES event has no event field, ignoring (key={})", key);
                return;
            }

            VesEventWrapper wrapper = new VesEventWrapper(event);
            if (!wrapper.isNotification()) {
                LOG.debug("VES event is not a notification, ignoring (key={})", key);
                return;
            }
            if (!wrapper.assertFields()) {
                LOG.warn("VES event fields invalid, ignoring (key={})", key);
                return;
            }

            String controllerUuid = wrapper.getControllerUuid();
            String nodeId = wrapper.getNodeId();
            String newState = wrapper.getNewState();

            // Validate controller is known
            if (config.findController(controllerUuid).isEmpty()) {
                LOG.warn("Unknown controller UUID '{}', ignoring event for node {}", controllerUuid, nodeId);
                return;
            }

            LOG.info("VES event: controller={}, node={}, state={}", controllerUuid, nodeId, newState);

            // Act on the lifecycle state
            switch (newState.toLowerCase()) {
                case "connecting", "connected" -> topologyWriter.writeNode(nodeId, controllerUuid, newState);
                case "disconnected" -> topologyWriter.deleteNode(nodeId);
                default -> LOG.warn("Unknown newState '{}' for node {}, ignoring", newState, nodeId);
            }

        } catch (Exception e) {
            LOG.error("Failed to process Kafka record (key={})", key, e);
        }
    }

    public void stop() {
        running.set(false);
        executor.shutdown();
        LOG.info("Kafka consumer stop requested");
    }
}
