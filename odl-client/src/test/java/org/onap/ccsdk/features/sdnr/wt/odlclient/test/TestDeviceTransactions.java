/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.OpendaylightClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotImplementedException;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.RemoteOpendaylightClient;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.ReadWriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.OpticalControlMode;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.connection.DestinationBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.connection.SourceBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.OrgOpenroadmDevice;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.RoadmConnections;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.RoadmConnectionsBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.RoadmConnectionsKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDeviceTransactions {
    private static final Logger LOG = LoggerFactory.getLogger(TestDeviceTransactions.class);

    private static final long TIMEOUT = 2;

    @Test
    public void testPut() throws NotImplementedException, URISyntaxException, InterruptedException, ExecutionException {
        String deviceId = "ROADM-A1";
        //RoadmConnections{_connectionName=DEG2-TTP-TXRX-SRG1-PP1-TXRX-1,
        //_destination=Destination{_dstIf=SRG1-PP1-TXRX-nmc-1, augmentation=[]},
        //_opticalControlMode=Off,
        //_source=Source{_srcIf=DEG2-TTP-TXRX-nmc-1,
        String connectionNumber = "DEG2-TTP-TXRX-SRG1-PP1-TXRX-1";
        RoadmConnectionsBuilder rdmConnBldr = new RoadmConnectionsBuilder()
                .setConnectionName(connectionNumber)
                .setOpticalControlMode(OpticalControlMode.Off)
                .setSource(new SourceBuilder().setSrcIf("DEG2-TTP-TXRX-nmc-1").build())
                .setDestination(new DestinationBuilder().setDstIf("SRG1-PP1-TXRX-nmc-1")
                .build());
        LOG.debug("try to post crossconnect {} in node {}",rdmConnBldr.build(),deviceId);

        InstanceIdentifier<RoadmConnections> rdmConnectionIID =
                InstanceIdentifier.create(OrgOpenroadmDevice.class)
                    .child(RoadmConnections.class, new RoadmConnectionsKey(rdmConnBldr.getConnectionName()));

        //192.168.80.4
        RemoteOpendaylightClient odlClient =
                new OpendaylightClient("http://localhost:8181", null, AuthMethod.BASIC, "admin", "admin");
        DataBroker dataBroker = odlClient.getRemoteDeviceDataBroker(deviceId);
        @NonNull
        ReadWriteTransaction rwtx = dataBroker.newReadWriteTransaction();
        rwtx.put(LogicalDatastoreType.CONFIGURATION, rdmConnectionIID, rdmConnBldr.build());
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        @NonNull
        CommitInfo result = rwtx.commit().withTimeout(TIMEOUT, TimeUnit.SECONDS, scheduledExecutor).get();
    }
}
