/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.device;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.MountPoint;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.common.device.DeviceTransaction;
import org.opendaylight.transportpce.common.device.DeviceTransactionManager;
import org.opendaylight.transportpce.sbrestconf.client.ControllerUuidResolver;
import org.opendaylight.transportpce.sbrestconf.client.SbRestconfClient;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DeviceTransactionManager implementation that uses RESTCONF instead of MDSAL mount points.
 * <p>
 * Instead of looking up a local NETCONF mount point via MountPointService, this manager resolves the controller-uuid
 * from MDSAL (via ControllerUuidResolver) and uses SbRestconfClient to communicate with devices through the remote
 * controller's RESTCONF API.
 * <p>
 *
 * This enables TransportPCE to manage devices that are connected to a remote OpenDaylight controller without requiring
 * local NETCONF mount points.
 */
public class RestDeviceTransactionManager implements DeviceTransactionManager {

    private static final Logger LOG = LoggerFactory.getLogger(RestDeviceTransactionManager.class);

    private final SbRestconfClient restconfClient;
    private final ControllerUuidResolver uuidResolver;
    private final DataBroker dataBroker;
    private final ScheduledExecutorService executor;
    private final ConcurrentMap<String, CountDownLatch> deviceLocks = new ConcurrentHashMap<>();

    /**
     * Creates a new RestDeviceTransactionManager.
     *
     * @param restconfClient the RESTCONF client for device access
     * @param uuidResolver   resolves controller-uuid from node-id via MDSAL
     * @param dataBroker     MDSAL DataBroker (for controller-uuid lookup fallback)
     */
    public RestDeviceTransactionManager(SbRestconfClient restconfClient,
            ControllerUuidResolver uuidResolver, DataBroker dataBroker) {
        this.restconfClient = restconfClient;
        this.uuidResolver = uuidResolver;
        this.dataBroker = dataBroker;
        this.executor = Executors.newScheduledThreadPool(4);
        LOG.info("RestDeviceTransactionManager created");
    }

    @Override
    public Future<Optional<DeviceTransaction>> getDeviceTransaction(String deviceId) {
        return getDeviceTransaction(deviceId, 15000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Future<Optional<DeviceTransaction>> getDeviceTransaction(String deviceId, long timeoutToSubmit,
            TimeUnit timeUnit) {
        CountDownLatch newLock = new CountDownLatch(1);
        return Executors.newSingleThreadExecutor().submit(() -> {
            LOG.debug("Creating RESTCONF transaction for device {}.", deviceId);

            // Wait for existing lock if present
            CountDownLatch actualLock = deviceLocks.put(deviceId, newLock);
            if (actualLock != null) {
                actualLock.await();
            }

            // Check if device is reachable via RESTCONF (controller-uuid must be resolvable)
            if (!isDeviceMounted(deviceId)) {
                LOG.error("Device {} not reachable via RESTCONF (no controller-uuid found)", deviceId);
                newLock.countDown();
                return Optional.empty();
            }

            LOG.debug("Created RESTCONF transaction for device {}.", deviceId);
            return Optional.of(new RestDeviceTransaction(restconfClient, deviceId, newLock));
        });
    }

    @Override
    public Optional<MountPoint> getDeviceMountPoint(String deviceId) {
        // No local mount point available — devices are accessed via RESTCONF
        // Return empty to indicate this is a remote device
        LOG.debug("getDeviceMountPoint called for {} — returning empty (RESTCONF mode)", deviceId);
        return Optional.empty();
    }

    @Override
    public <T extends DataObject> Optional<T> getDataFromDevice(String deviceId,
            LogicalDatastoreType logicalDatastoreType, DataObjectIdentifier<T> path,
            long timeout, TimeUnit timeUnit) {
        LOG.debug("Reading from device {} via RESTCONF: {}", deviceId, path);

        try {
            Optional<T> result = restconfClient.get(deviceId, path, path.lastStep().type());
            if (result.isEmpty()) {
                LOG.debug("No data found at {} on device {}", path, deviceId);
            }
            return result;
        } catch (Exception e) {
            LOG.error("Failed to read from device {} via RESTCONF at path {}", deviceId, path, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isDeviceMounted(String deviceId) {
        // Check if controller-uuid is resolvable for this device
        Optional<String> controllerUuid = uuidResolver.resolveControllerUuid(deviceId);
        if (controllerUuid.isEmpty()) {
            LOG.debug("Device {} not mounted — no controller-uuid found", deviceId);
            return false;
        }
        LOG.debug("Device {} is mounted via controller {}", deviceId, controllerUuid.orElseThrow());
        return true;
    }

    /**
     * Shutdown executor threads.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
