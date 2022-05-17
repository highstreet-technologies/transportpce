/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.rev170206;

import com.google.common.util.concurrent.ListenableFuture;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.BaseRemoteRpcImpl;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.DisableAutomaticShutoffInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.DisableAutomaticShutoffOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.GetConnectionPortTrailInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.GetConnectionPortTrailOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.OrgOpenroadmDeviceService;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.SetCurrentDatetimeInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.SetCurrentDatetimeOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.StartScanInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev170206.StartScanOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class RemoteOrgOpenroadmDeviceServiceImpl extends BaseRemoteRpcImpl
        implements OrgOpenroadmDeviceService {

    public RemoteOrgOpenroadmDeviceServiceImpl(RestconfHttpClient client, String nodeId) {
        super(client, nodeId);
    }

    @Override
    public ListenableFuture<RpcResult<GetConnectionPortTrailOutput>> getConnectionPortTrail(
            GetConnectionPortTrailInput input) {
        return this.execute("org-openroadm-device:get-connection-port-trail", input,
                GetConnectionPortTrailOutput.class);
    }

    @Override
    public ListenableFuture<RpcResult<DisableAutomaticShutoffOutput>> disableAutomaticShutoff(
            DisableAutomaticShutoffInput input) {
        return this.execute("org-openroadm-device:disable-automatic-shutoff", input,
                DisableAutomaticShutoffOutput.class);
    }

    @Override
    public ListenableFuture<RpcResult<StartScanOutput>> startScan(StartScanInput input) {
        return this.execute("org-openroadm-device:start-scan", input, StartScanOutput.class);
    }

    @Override
    public ListenableFuture<RpcResult<SetCurrentDatetimeOutput>> setCurrentDatetime(
            SetCurrentDatetimeInput input) {
        return this.execute("org-openroadm-device:set-current-datetime", input,
                SetCurrentDatetimeOutput.class);
    }

}
