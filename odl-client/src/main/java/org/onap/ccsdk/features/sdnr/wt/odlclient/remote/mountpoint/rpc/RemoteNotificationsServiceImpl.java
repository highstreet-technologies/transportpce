/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc;

import com.google.common.util.concurrent.ListenableFuture;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.notification._1._0.rev080714.CreateSubscriptionInput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.notification._1._0.rev080714.CreateSubscriptionOutput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.notification._1._0.rev080714.CreateSubscriptionOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.netconf.notification._1._0.rev080714.NotificationsService;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

public class RemoteNotificationsServiceImpl extends BaseRemoteRpcImpl implements NotificationsService {

    public RemoteNotificationsServiceImpl(RestconfHttpClient client, String nodeId) {
        super(client, nodeId);
    }

    @Override
    public ListenableFuture<RpcResult<CreateSubscriptionOutput>> createSubscription(
            CreateSubscriptionInput input) {
        Builder<CreateSubscriptionOutput> outputBuilder = new CreateSubscriptionOutputBuilder();
        @NonNull
        RpcResultBuilder<CreateSubscriptionOutput> result = RpcResultBuilder.success(outputBuilder);
        return result.buildFuture();
    }

}
