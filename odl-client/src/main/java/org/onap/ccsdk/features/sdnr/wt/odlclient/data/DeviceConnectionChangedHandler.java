/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;

public interface DeviceConnectionChangedHandler {

    void onRemoteDeviceConnected(String nodeId, NetconfNode netconfNode);

    void onRemoteDeviceDisConnected(String nodeId);

    void onRemoteDeviceUnableToConnect(String nodeId);

    void onRemoteDeviceConnecting(String nodeId);
}
