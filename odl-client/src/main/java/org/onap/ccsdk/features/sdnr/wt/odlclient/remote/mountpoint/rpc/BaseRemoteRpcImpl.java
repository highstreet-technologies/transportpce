/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc;

import com.google.common.util.concurrent.ListenableFuture;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class BaseRemoteRpcImpl {

    private String nodeId;
    private RestconfHttpClient client;

    public BaseRemoteRpcImpl(RestconfHttpClient client, String nodeId) {
        this.client = client;
        this.nodeId = nodeId;
    }

    protected <O extends DataObject, I extends DataObject> ListenableFuture<RpcResult<O>> execute(
            String rpc, I input, Class<O> clazz) {
        return this.client.executeRpc(this.nodeId, rpc, input, clazz);

    }
}
