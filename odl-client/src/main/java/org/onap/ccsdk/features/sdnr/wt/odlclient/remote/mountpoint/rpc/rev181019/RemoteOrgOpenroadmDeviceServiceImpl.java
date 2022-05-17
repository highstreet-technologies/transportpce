/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.rev181019;

import com.google.common.util.concurrent.ListenableFuture;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint.rpc.BaseRemoteRpcImpl;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.CreateTechInfoInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.CreateTechInfoOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.DisableAutomaticShutoffInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.DisableAutomaticShutoffOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.GetConnectionPortTrailInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.GetConnectionPortTrailOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.LedControlInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.LedControlOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.OrgOpenroadmDeviceService;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.SetCurrentDatetimeInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.SetCurrentDatetimeOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.StartScanInput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.StartScanOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class RemoteOrgOpenroadmDeviceServiceImpl extends BaseRemoteRpcImpl
        implements OrgOpenroadmDeviceService {

    public RemoteOrgOpenroadmDeviceServiceImpl(RestconfHttpClient client, String nodeId) {
        super(client, nodeId);
    }

    @Override
    public ListenableFuture<RpcResult<LedControlOutput>> ledControl(LedControlInput input) {
        return this.execute("org-openroadm-device:led-control", input, LedControlOutput.class);
    }

    @Override
    public ListenableFuture<RpcResult<CreateTechInfoOutput>> createTechInfo(CreateTechInfoInput input) {
        return this.execute("org-openroadm-device:create-tech-info", input, CreateTechInfoOutput.class);
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
