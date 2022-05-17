/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote;

import com.google.common.util.concurrent.FluentFuture;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.mdsal.binding.api.ReadWriteTransaction;
import org.opendaylight.mdsal.binding.api.TransactionChain;
import org.opendaylight.mdsal.binding.api.TransactionChainListener;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.util.concurrent.FluentFutures;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteDataBroker implements DataBroker {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteDataBroker.class);
    protected final RestconfHttpClient remoteOdlClient;

    public RemoteDataBroker(RestconfHttpClient restClient) {
        this.remoteOdlClient = restClient;
    }

    @Override
    public <T extends DataObject, L extends DataTreeChangeListener<T>> @NonNull ListenerRegistration<L>
        registerDataTreeChangeListener(@NonNull DataTreeIdentifier<T> treeId, @NonNull L listener) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull ReadTransaction newReadOnlyTransaction() {
        return new RemoteReadOnlyTransaction(this.remoteOdlClient);
    }

    @Override
    public @NonNull ReadWriteTransaction newReadWriteTransaction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull WriteTransaction newWriteOnlyTransaction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull TransactionChain createTransactionChain(@NonNull TransactionChainListener listener) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull TransactionChain createMergingTransactionChain(
            @NonNull TransactionChainListener listener) {
        // TODO Auto-generated method stub
        return null;
    }

    private static final class RemoteReadOnlyTransaction implements ReadTransaction {

        private final RestconfHttpClient client;

        private RemoteReadOnlyTransaction(RestconfHttpClient remoteOdlClient) {
            this.client = remoteOdlClient;
        }

        @Override
        public <T extends DataObject> @NonNull FluentFuture<java.util.Optional<T>> read(
                @NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<T> path) {
            try {
                return this.client.read(store, path);
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException | IOException e) {
                LOG.warn("problem reading data:", e);
                return FluentFutures.immediateFailedFluentFuture(e);
            }

        }

        @Override
        public @NonNull FluentFuture<Boolean> exists(@NonNull LogicalDatastoreType store,
                @NonNull InstanceIdentifier<?> path) {
            // TODO Auto-generated method stub
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

}
