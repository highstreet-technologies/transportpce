/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf;

import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.transportpce.sbrestconf.client.ControllerUuidResolver;
import org.opendaylight.transportpce.sbrestconf.client.SbRestconfClient;
import org.opendaylight.transportpce.sbrestconf.config.SbRestconfConfig;
import org.opendaylight.transportpce.sbrestconf.config.SbRestconfConfigLoader;
import org.opendaylight.yangtools.binding.data.codec.api.BindingDataCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for the southbound RESTCONF client.
 *
 * On startup loads configuration from a properties file, creates the
 * ControllerUuidResolver (reads controller-uuid from MDSAL), and the
 * SbRestconfClient (type-safe RESTCONF operations using DataObject
 * and DataObjectIdentifier with BindingDataCodec for serialization).
 *
 * Works both in OSGi/Karaf (via blueprint) and Lighty (via direct instantiation).
 */
public class SbRestconfProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SbRestconfProvider.class);
    private static final String DEFAULT_CONFIG_PATH = "etc/org.opendaylight.transportpce.cfg";

    private final DataBroker dataBroker;
    private final String configPath;
    private final BindingDataCodec dataCodec;
    private SbRestconfClient client;

    /**
     * Constructor with default config path — for OSGi/Karaf.
     */
    public SbRestconfProvider(DataBroker dataBroker, BindingDataCodec dataCodec) {
        this(dataBroker, DEFAULT_CONFIG_PATH, dataCodec);
    }

    /**
     * Constructor with explicit config path — for Lighty and tests.
     */
    public SbRestconfProvider(DataBroker dataBroker, String configPath, BindingDataCodec dataCodec) {
        this.dataBroker = dataBroker;
        this.configPath = configPath;
        this.dataCodec = dataCodec;
        LOG.info("SbRestconfProvider created with configPath: {}", configPath);
    }

    /**
     * Load config and start the RESTCONF client.
     * Called by OSGi blueprint or Lighty module.
     */
    public void start() {
        LOG.info("Starting sb-restconf client");

        SbRestconfConfig config = SbRestconfConfigLoader.load(configPath);
        ControllerUuidResolver resolver = new ControllerUuidResolver(dataBroker);
        this.client = new SbRestconfClient(config, resolver, dataCodec);

        LOG.info("sb-restconf client started");
    }

    public SbRestconfClient getClient() {
        return client;
    }

    @Override
    public void close() {
        LOG.info("SbRestconfProvider shutting down");
        if (client != null) {
            client.close();
        }
    }
}
