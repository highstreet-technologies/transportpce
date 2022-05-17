/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint;

import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.RemoteNotificationsServiceImpl;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.rev170206.RemoteOrgOpenroadmDeviceServiceImpl;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.BindingService;
import org.opendaylight.mdsal.binding.api.RpcConsumerRegistry;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.OrgOpenroadmDeviceService;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.notification._1._0.rev080714.NotificationsService;
import org.opendaylight.yangtools.yang.binding.RpcService;

public class RemoteRpcConsumerRegistry implements RpcConsumerRegistry, BindingService {

    private final RestconfHttpClient client;
    private final String nodeId;

    public RemoteRpcConsumerRegistry(RestconfHttpClient restClient, String nodeId) {
        this.client = restClient;
        this.nodeId = nodeId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RpcService> @NonNull T getRpcService(@NonNull Class<T> serviceInterface) {
        // OrgOpenroadmDeviceService.class
        if (serviceInterface.equals(OrgOpenroadmDeviceService.class)) {
            return (@NonNull T) new RemoteOrgOpenroadmDeviceServiceImpl(this.client, this.nodeId);
        }
        if (serviceInterface.equals(
                org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.OrgOpenroadmDeviceService.class)) {
            return (@NonNull T) new org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.rev181019.RemoteOrgOpenroadmDeviceServiceImpl(
                    this.client, this.nodeId);
        }
        // NotificationsService.class
        if (serviceInterface.equals(NotificationsService.class)) {
            return (@NonNull T) new RemoteNotificationsServiceImpl(this.client, this.nodeId);
        }
        return (@NonNull T) new RpcService() {
        };
    }

}
