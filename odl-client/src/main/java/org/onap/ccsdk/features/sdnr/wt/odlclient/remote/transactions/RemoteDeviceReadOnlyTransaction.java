/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.transactions;

import com.google.common.util.concurrent.FluentFuture;
import java.io.IOException;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yangtools.util.concurrent.FluentFutures;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class RemoteDeviceReadOnlyTransaction extends RemoteTransaction implements ReadTransaction {

    public RemoteDeviceReadOnlyTransaction(RestconfHttpClient remoteOdlClient, String nodeId) {
        super(remoteOdlClient, nodeId);
    }

    @Override
    public <T extends DataObject> @NonNull FluentFuture<Optional<T>> read(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<T> path) {
        try {
            return this.client.read(store, path, this.nodeId);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException | IOException e) {
            return FluentFutures.immediateFailedFluentFuture(e);
        }
    }

    @Override
    public @NonNull FluentFuture<Boolean> exists(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<?> path) {
        return null;
    }

    @Override
    public @NonNull Object getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}