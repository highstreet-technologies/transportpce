/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.transactions;

import com.google.common.util.concurrent.FluentFuture;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yangtools.util.concurrent.FluentFutures;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteWriteOnlyTransaction extends RemoteTransaction implements WriteTransaction {


    private static final Logger LOG = LoggerFactory.getLogger(RemoteWriteOnlyTransaction.class);

    public RemoteWriteOnlyTransaction(RestconfHttpClient remoteOdlClient, String nodeId) {
        super(remoteOdlClient, nodeId);
    }

    @Override
    public @NonNull Object getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataObject> void put(@NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<T> path,
            @NonNull T data) {
        LOG.warn("w transaction not yet implemented: put, {}", whoCalledMeAll());
    }

    @Override
    public <T extends DataObject> void mergeParentStructurePut(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<T> path, @NonNull T data) {
        LOG.warn("w transaction not yet implemented: mergeParentStructurePut, {}", whoCalledMeAll());

    }

    @Override
    public <T extends DataObject> void merge(@NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<T> path,
            @NonNull T data) {
        LOG.warn("w transaction not yet implemented: merge, {}", whoCalledMeAll());

    }

    @Override
    public <T extends DataObject> void mergeParentStructureMerge(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<T> path, @NonNull T data) {
        LOG.warn("w transaction not yet implemented: mergeParentStructureMerge{}", whoCalledMeAll());
    }

    @Override
    public void delete(@NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<?> path) {
        LOG.warn("w transaction not yet implemented: mergeParentStructureMerge {}", whoCalledMeAll());
    }

    @Override
    public boolean cancel() {

        return false;
    }

    @Override
    public @NonNull FluentFuture<? extends @NonNull CommitInfo> commit() {
        LOG.warn("w transaction not yet implemented: commit, {}", whoCalledMeAll());

        CommitInfo result = new CommitInfo() {

        };
        this.pushSuccess(result, COMMIT_DELAY);
        return FluentFutures.immediateFluentFuture(result);

    }



}
