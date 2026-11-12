/*
 * Copyright (c) 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.replicate.netty;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.mdsal.singleton.api.ClusterSingletonServiceProvider;
import org.opendaylight.yangtools.concepts.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-OSGI replacement for the OSGi Declarative Services components {@link NettyReplicationSource} and
 * {@link NettyReplicationSink}, intended for lighty.io deployments.
 *
 * <p>Reads the same configuration files as OpenDaylight Karaf distributions, relative to the working directory:
 * <ul>
 *   <li>{@code etc/org.opendaylight.mdsal.replicate.netty.source.cfg}</li>
 *   <li>{@code etc/org.opendaylight.mdsal.replicate.netty.sink.cfg}</li>
 * </ul>
 *
 * <p>When a configuration file is missing, the OpenDaylight defaults apply, which have replication disabled.
 */
public final class ReplicationService implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicationService.class);

    private static final String SOURCE_CONFIG_FILE = "etc/org.opendaylight.mdsal.replicate.netty.source.cfg";
    private static final String SINK_CONFIG_FILE = "etc/org.opendaylight.mdsal.replicate.netty.sink.cfg";

    private final DOMDataBroker dataBroker;
    private final ClusterSingletonServiceProvider singletonService;
    private final AbstractBootstrapSupport bootstrapSupport;

    private Registration sourceRegistration;
    private Registration sinkRegistration;

    public ReplicationService(final DOMDataBroker dataBroker, final ClusterSingletonServiceProvider singletonService) {
        this.dataBroker = Objects.requireNonNull(dataBroker);
        this.singletonService = Objects.requireNonNull(singletonService);
        this.bootstrapSupport = AbstractBootstrapSupport.create();
    }

    /**
     * Start datastore replication according to the configuration files. Both source and sink may be enabled at the
     * same time, just like in an OSGi deployment.
     *
     * @throws IOException if the sink source host cannot be resolved
     */
    public void start() throws IOException {
        startSource();
        startSink();
    }

    private void startSource() {
        final Properties props = loadConfig(SOURCE_CONFIG_FILE);
        final boolean enabled = Boolean.parseBoolean(props.getProperty("enabled", "false"));
        final int listenPort = Integer.parseInt(props.getProperty("listen-port", "9999"));
        final int keepaliveInterval = Integer.parseInt(props.getProperty("keepalive-interval-seconds", "10"));
        final int maxMissedKeepalives = Integer.parseInt(props.getProperty("max-missed-keepalives", "5"));
        LOG.info("Starting MD-SAL datastore replication source: enabled={}, listen-port={}", enabled, listenPort);
        sourceRegistration = NettyReplicationSource.createSource(bootstrapSupport, dataBroker, singletonService,
            enabled, listenPort, Duration.ofSeconds(keepaliveInterval), maxMissedKeepalives);
    }

    private void startSink() throws IOException {
        final Properties props = loadConfig(SINK_CONFIG_FILE);
        final boolean enabled = Boolean.parseBoolean(props.getProperty("enabled", "false"));
        final String sourceHost = props.getProperty("source-host", "127.0.0.1");
        final int sourcePort = Integer.parseInt(props.getProperty("source-port", "9999"));
        final int reconnectDelay = Integer.parseInt(props.getProperty("reconnect-delay-millis", "3000"));
        final int keepaliveInterval = Integer.parseInt(props.getProperty("keepalive-interval-seconds", "10"));
        final int maxMissedKeepalives = Integer.parseInt(props.getProperty("max-missed-keepalives", "5"));
        LOG.info("Starting MD-SAL datastore replication sink: enabled={}, source={}:{}", enabled, sourceHost,
            sourcePort);
        try {
            sinkRegistration = NettyReplicationSink.createSink(bootstrapSupport, dataBroker, singletonService,
                enabled, InetAddress.getByName(sourceHost), sourcePort, Duration.ofMillis(reconnectDelay),
                Duration.ofSeconds(keepaliveInterval), maxMissedKeepalives);
        } catch (UnknownHostException e) {
            throw new IOException("Cannot resolve replication source host " + sourceHost, e);
        }
    }

    private static Properties loadConfig(final String configPath) {
        final Path path = Path.of(configPath);
        final Properties props = new Properties();
        if (Files.exists(path)) {
            try (InputStream is = Files.newInputStream(path)) {
                props.load(is);
            } catch (IOException e) {
                LOG.warn("Failed to load replication config {}, using defaults", configPath, e);
            }
        } else {
            LOG.info("Replication config {} not found, using defaults (replication disabled)", configPath);
        }
        return props;
    }

    @Override
    public void close() {
        if (sourceRegistration != null) {
            sourceRegistration.close();
            sourceRegistration = null;
        }
        if (sinkRegistration != null) {
            sinkRegistration.close();
            sinkRegistration = null;
        }
        try {
            bootstrapSupport.close();
        } catch (InterruptedException e) {
            LOG.warn("Interrupted while shutting down replication netty event loop groups", e);
            Thread.currentThread().interrupt();
        }
    }
}
