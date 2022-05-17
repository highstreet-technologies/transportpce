/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote;

import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.transactions.RemoteDeviceReadOnlyTransaction;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.transactions.RemoteDeviceReadWriteTransaction;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.transactions.RemoteWriteOnlyTransaction;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.mdsal.binding.api.ReadWriteTransaction;
import org.opendaylight.mdsal.binding.api.WriteTransaction;

public class RemoteDeviceDataBroker extends RemoteDataBroker {


    private final String nodeId;

    public RemoteDeviceDataBroker(RestconfHttpClient odlClient, String nodeId) {
        super(odlClient);
        this.nodeId = nodeId;
    }

    @Override
    public ReadTransaction newReadOnlyTransaction() {
        return new RemoteDeviceReadOnlyTransaction(this.remoteOdlClient, this.nodeId);
    }

    @Override
    public @NonNull ReadWriteTransaction newReadWriteTransaction() {
        return new RemoteDeviceReadWriteTransaction(this.remoteOdlClient, this.nodeId);
    }
    @Override
    public @NonNull WriteTransaction newWriteOnlyTransaction() {
        return new RemoteWriteOnlyTransaction(this.remoteOdlClient, this.nodeId);
    }

}
