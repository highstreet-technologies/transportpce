/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.MountPoint;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public interface RemoteOpendaylightClient<N extends Node, D extends DataTreeChangeListener<N>> {

    DataBroker getRemoteDeviceDataBroker(String nodeId);

    DataBroker getRemoteDataBroker();

    boolean isDevicePresent(String nodeId);

    MountPoint getMountPoint(String deviceId);

    @NonNull
    ListenerRegistration<D> registerDataTreeChangeListener(@NonNull DataTreeIdentifier<N> treeId,
            @NonNull D listener);

    void registerDeviceConnectionChangeListener(DeviceConnectionChangedHandler listener);

    boolean isEnabled();

    void unregisterDeviceConnectionChangeListener(DeviceConnectionChangedHandler listener);

    <T extends DataObject> Optional<T> getDataFromDevice(String nodeId, LogicalDatastoreType configuration,
            InstanceIdentifier<T> xciid, long deviceReadTimeout, TimeUnit deviceReadTimeoutUnit) throws
        InterruptedException, TimeoutException, ExecutionException;

	boolean isDeviceMounted(String nodeId);

}