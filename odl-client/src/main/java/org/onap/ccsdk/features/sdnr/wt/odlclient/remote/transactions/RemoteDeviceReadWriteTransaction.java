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
import java.util.concurrent.ExecutionException;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.ReadWriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yangtools.util.concurrent.FluentFutures;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteDeviceReadWriteTransaction extends RemoteTransaction implements ReadWriteTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteDeviceReadWriteTransaction.class);
    private FluentFuture<?> futureRequest;

    public RemoteDeviceReadWriteTransaction(RestconfHttpClient remoteOdlClient, String nodeId) {
        super(remoteOdlClient, nodeId);
        this.futureRequest = null;
    }

    @Override
    public boolean cancel() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public @NonNull FluentFuture<? extends @NonNull CommitInfo> commit() {
        CommitInfo result = new CommitInfo() {

        };
        //       LOG.warn("rw transaction not yet implemented: commit, {}",whoCalledMeAll());
        if (this.futureRequest != null) {
            try {
                this.futureRequest.get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.warn("problem executing request: ", e);
            }
        }
        this.pushSuccess(result, COMMIT_DELAY);
        return FluentFutures.immediateFluentFuture(result);
    }

    @Override
    public @NonNull Object getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataObject> void put(@NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<T> path,
            @NonNull T data) {
        LOG.debug("rw transaction now implemented: put");
        try {
            this.futureRequest = this.client.put(store, path, data, this.nodeId);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e) {
            LOG.warn("problem creating put request: ", e);
        }
    }

//    @Override
//    public <T extends DataObject> void put(@NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<T> path,
//            @NonNull T data, boolean createMissingParents) {
//        LOG.debug("rw transaction now implemented: deprecated put");
//        try {
//            this.futureRequest = this.client.put(store, path, data, this.nodeId);
//        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
//                | IllegalAccessException e) {
//            LOG.warn("problem creating put request: ", e);
//        }
//    }

    @Override
    public <T extends DataObject> void mergeParentStructurePut(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<T> path, @NonNull T data) {
        LOG.warn("rw transaction not yet implemented: mergeParentStructurePut, {}", whoCalledMeAll());
    }

//    @Override
//    public <T extends DataObject> void merge(@NonNull LogicalDatastoreType store,
//            @NonNull InstanceIdentifier<T> instanceIdentifier,
//            @NonNull T data, boolean createMissingParents) {
//        LOG.debug("rw transaction now implemented: deprecated merge ");
//        try {
//            this.futureRequest = this.client.merge(store, instanceIdentifier, data, this.nodeId);
//        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
//                | IllegalAccessException e) {
//            LOG.warn("problem creating merge request: ", e);
//        }
//    }

    @Override
    public <T extends DataObject> void merge(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<T> instanceIdentifier, @NonNull T data) {
        LOG.debug("rw transaction now implemented: merge ");
        try {
            this.futureRequest = this.client.merge(store, instanceIdentifier, data, this.nodeId);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e) {
            LOG.warn("problem creating merge request: ", e);
        }
    }

    @Override
    public <T extends DataObject> void mergeParentStructureMerge(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<T> path, @NonNull T data) {
        LOG.warn("rw transaction not yet implemented: mergeParentStructureMerge, {}", whoCalledMeAll());
    }

    @Override
    public void delete(@NonNull LogicalDatastoreType store, @NonNull InstanceIdentifier<?> path) {
        LOG.debug("rw transaction now implemented: delete ");
        try {
            this.futureRequest = this.client.delete(store, path, this.nodeId);
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchFieldException
                | IllegalAccessException e) {
            LOG.warn("problem deleting {} in {} for node {}: ", path, store, this.nodeId, e);
        }
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
        LOG.warn("rw transaction not yet implemented: mergeParentStructurePut, {}", whoCalledMeAll());
        return null;
    }

}
