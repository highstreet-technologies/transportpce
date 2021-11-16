/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotImplementedException;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlRpcObjectMapperXml;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.RemoteMountPoint;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.MountPoint;
import org.opendaylight.mdsal.binding.api.RpcConsumerRegistry;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.LedControlInputBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.LedControlOutput;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.OrgOpenroadmDeviceService;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.led.control.input.equipment.entity.CircuitPackBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.OrgOpenroadmDevice;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.Protocols;
import org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.Protocols1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.lldp.container.Lldp;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRemoteMountpointService {

    private static final Logger LOG = LoggerFactory.getLogger(TestRemoteMountpointService.class);

    private static final String DEVICEID = "onapextroadma1";
    private static final String ODL_USERNAME = "admin";
    //private static final String ODL_PASSWD = "Kp8bJ4SXszM0WXlhak3eHlcse2gAw84vaoGGmJvUy2U";
    private static final String ODL_PASSWD = "admin";
    private static final String BASEURL = "http://192.168.16.3:8181";

    //@Test
    public void test() throws Exception {
        RestconfHttpClient restClient =
                new RestconfHttpClient(BASEURL, false, AuthMethod.BASIC, ODL_USERNAME, ODL_PASSWD);
        MountPoint mountPoint = new RemoteMountPoint(restClient,null, DEVICEID);
        final Optional<RpcConsumerRegistry> service = mountPoint.getService(RpcConsumerRegistry.class);
        if (!service.isPresent()) {
            LOG.error("Failed to get RpcService for node {}", DEVICEID);
        }
        final OrgOpenroadmDeviceService rpcService = service.get().getRpcService(OrgOpenroadmDeviceService.class);
        final LedControlInputBuilder builder = new LedControlInputBuilder();
        builder.setEnabled(true).setEquipmentEntity(new CircuitPackBuilder().setCircuitPackName("1/0").build());
        final Future<RpcResult<LedControlOutput>> output = rpcService.ledControl(builder.build());
        LOG.info("{}",output);
    }

    //@Test
    public void testRpcDeserializer() throws IOException {
        OdlRpcObjectMapperXml mapper = new OdlRpcObjectMapperXml();
        LedControlOutput output = mapper.readValue(
                "<output xmlns=\"http://org/openroadm/device\">\n"
                + "    <status>Successful</status>\n"
                + "    <status-message>test</status-message>\n"
                + "</output>", LedControlOutput.class);
        LOG.info("{}",output);
    }

    @Test
    public void testAugment() throws NotImplementedException, ClassNotFoundException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException,
            ExecutionException, IOException {
        RestconfHttpClient restClient = new RestconfHttpClient(BASEURL, false, AuthMethod.BASIC,
                ODL_USERNAME, ODL_PASSWD);
        InstanceIdentifier<Protocols> protocoliid =
                InstanceIdentifier.create(OrgOpenroadmDevice.class).child(Protocols.class);
        Optional<Protocols> protocolObject =
                restClient.read(LogicalDatastoreType.OPERATIONAL, protocoliid,"onapext3roadma1").get();
        if (protocolObject.isPresent()) {
            LOG.info("pro={}",protocolObject);
            @Nullable
            //Protocols1 pr = protocolObject.get().augmentation(Protocols1.class);
            Protocols1 pr = restClient.read(LogicalDatastoreType.OPERATIONAL,
                    protocoliid.augmentation(Protocols1.class),"onapext3roadma1").get().get();
            LOG.info("pr={}",pr);
            Lldp lldp = pr.getLldp();
            LOG.info("{}",lldp);
        }
    }

}
