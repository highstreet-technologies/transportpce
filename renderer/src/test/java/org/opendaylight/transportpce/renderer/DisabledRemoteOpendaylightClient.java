/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.renderer;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.DeviceConnectionChangedHandler;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.RemoteOpendaylightClient;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.MountPoint;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class DisabledRemoteOpendaylightClient implements RemoteOpendaylightClient<Node, DataTreeChangeListener<Node>> {

    @Override
    public DataBroker getRemoteDeviceDataBroker(String nodeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataBroker getRemoteDataBroker() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDevicePresent(String nodeId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MountPoint getMountPoint(String deviceId) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public void registerDeviceConnectionChangeListener(DeviceConnectionChangedHandler listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void unregisterDeviceConnectionChangeListener(DeviceConnectionChangedHandler listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends DataObject> Optional<T> getDataFromDevice(String nodeId,
            LogicalDatastoreType configuration, InstanceIdentifier<T> xciid, long deviceReadTimeout,
            TimeUnit deviceReadTimeoutUnit)
            throws InterruptedException, TimeoutException, ExecutionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull ListenerRegistration<DataTreeChangeListener<Node>> registerDataTreeChangeListener(
            @NonNull DataTreeIdentifier<Node> treeId, @NonNull DataTreeChangeListener<Node> listener) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDeviceMounted(String nodeId) {
        // TODO Auto-generated method stub
        return false;
    }



}