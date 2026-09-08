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
import org.opendaylight.yangtools.binding.data.codec.api.BindingDataCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for the southbound RESTCONF client.
 *
 * Creates the ControllerUuidResolver (reads controller-uuid from MDSAL),
 * and the SbRestconfClient (type-safe RESTCONF operations using DataObject
 * and DataObjectIdentifier with BindingDataCodec for serialization).
 *
 * Works both in OSGi/Karaf (via blueprint) and Lighty (via direct instantiation).
 */
public class SbRestconfProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SbRestconfProvider.class);

    private final SbRestconfClient client;

    public SbRestconfProvider(DataBroker dataBroker, SbRestconfConfig config,
            BindingDataCodec dataCodec) {
        ControllerUuidResolver resolver = new ControllerUuidResolver(dataBroker);
        this.client = new SbRestconfClient(config, resolver, dataCodec);
        LOG.info("SbRestconfProvider created");
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
