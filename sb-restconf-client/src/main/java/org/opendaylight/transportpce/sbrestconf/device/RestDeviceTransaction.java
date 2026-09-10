/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.device;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.common.device.DeviceTransaction;
import org.opendaylight.transportpce.sbrestconf.client.SbRestconfClient;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.util.concurrent.FluentFutures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DeviceTransaction implementation that uses RESTCONF instead of MDSAL mount points.
 *
 * Collects put/merge/delete operations and executes them on commit() as individual
 * RESTCONF requests via SbRestconfClient. read() is executed immediately as a GET.
 */
public class RestDeviceTransaction extends DeviceTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(RestDeviceTransaction.class);

    private final SbRestconfClient restconfClient;
    private final String nodeId;
    private final List<PendingOperation> pendingOperations = new ArrayList<>();

    private enum OperationType { PUT, MERGE, DELETE }

    private record PendingOperation(OperationType type, DataObjectIdentifier<?> path,
            DataObject data, LogicalDatastoreType store) {}

    /**
     * Creates a new RestDeviceTransaction.
     *
     * @param restconfClient the RESTCONF client for device access
     * @param nodeId the device node-id
     * @param deviceLock the lock for this device
     */
    public RestDeviceTransaction(SbRestconfClient restconfClient, String nodeId, CountDownLatch deviceLock) {
        super(deviceLock);
        this.restconfClient = restconfClient;
        this.nodeId = nodeId;
        LOG.debug("RestDeviceTransaction created for node {}", nodeId);
    }

    @Override
    public <T extends DataObject> ListenableFuture<Optional<T>> read(
            LogicalDatastoreType store, DataObjectIdentifier<T> path) {
        Optional<T> result = restconfClient.get(nodeId, path, path.lastStep().type());
        return FluentFutures.immediateFluentFuture(result);
    }

    @Override
    public <T extends DataObject> void put(LogicalDatastoreType store, DataObjectIdentifier<T> path, T data) {
        pendingOperations.add(new PendingOperation(OperationType.PUT, path, data, store));
    }

    @Override
    public <T extends DataObject> void merge(LogicalDatastoreType store, DataObjectIdentifier<T> path, T data) {
        pendingOperations.add(new PendingOperation(OperationType.MERGE, path, data, store));
    }

    @Override
    public void delete(LogicalDatastoreType store, DataObjectIdentifier<?> path) {
        pendingOperations.add(new PendingOperation(OperationType.DELETE, path, null, store));
    }

    @Override
    public boolean cancel() {
        if (wasSubmittedOrCancelled().get()) {
            LOG.warn("Transaction was already submitted or canceled!");
            return false;
        }
        LOG.debug("Transaction cancelled for node {}", nodeId);
        wasSubmittedOrCancelled().set(true);
        pendingOperations.clear();
        afterClose();
        return true;
    }

    @Override
    public FluentFuture<? extends CommitInfo> commit(long timeout, TimeUnit timeUnit) {
        if (wasSubmittedOrCancelled().get()) {
            String msg = "Transaction was already submitted or canceled!";
            LOG.error(msg);
            return FluentFutures.immediateFailedFluentFuture(new IllegalStateException(msg));
        }

        LOG.debug("Committing transaction for node {} with {} operations", nodeId, pendingOperations.size());
        wasSubmittedOrCancelled().set(true);

        boolean allSuccess = true;
        for (PendingOperation op : pendingOperations) {
            try {
                boolean success = executeOperation(op);
                if (!success) {
                    allSuccess = false;
                    LOG.error("Failed to execute {} on node {} at path {}", op.type(), nodeId, op.path());
                }
            } catch (Exception e) {
                allSuccess = false;
                LOG.error("Exception executing {} on node {} at path {}", op.type(), nodeId, op.path(), e);
            }
        }
        pendingOperations.clear();
        afterClose();

        if (allSuccess) {
            return FluentFutures.immediateFluentFuture(CommitInfo.empty());
        } else {
            return FluentFutures.immediateFailedFluentFuture(
                    new RuntimeException("One or more RESTCONF operations failed for node " + nodeId));
        }
    }

    @SuppressWarnings("unchecked")
    private boolean executeOperation(PendingOperation op) {
        return switch (op.type()) {
            case PUT -> restconfClient.put(nodeId, (DataObjectIdentifier<DataObject>) op.path(), op.data());
            case MERGE -> restconfClient.patch(nodeId, (DataObjectIdentifier<DataObject>) op.path(), op.data());
            case DELETE -> restconfClient.delete(nodeId, op.path());
        };
    }

    private void afterClose() {
        pendingOperations.clear();
        // deviceLock.countDown() is called in parent afterClose
    }
}
