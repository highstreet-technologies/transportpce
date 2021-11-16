/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.OrgOpenroadmDevice;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.Info;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.OduConnection;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.OduConnectionKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.SharedRiskGroup;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.SharedRiskGroupKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.optional.rev190614.netconf.node.fields.optional.Topology;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestIifToUri {

    private static final Logger LOG = LoggerFactory.getLogger(TestIifToUri.class);
    private static final String ROADMAA_INFO = "/rests/data/network-topology:network-topology/topology=topology-netconf"
                + "/node=roadmaa/yang-ext:mount/org-openroadm-device:org-openroadm-device/info";
    private static final String ROADMAA_NODEID = "roadmaa";
    private static final Object ROADMAA_SRG_1 = "/rests/data/network-topology:network-topology/topology=topology-"
            + "netconf/node=roadmaa/yang-ext:mount/org-openroadm-device:org-openroadm-device/shared-risk-group=1";

    @Test
    public void test1() throws Exception {

        TestRestconfHttpClient restconfClient = new TestRestconfHttpClient("http://localhost:8181/", false, AuthMethod.BASIC,"","");
        String uri;
        uri = restconfClient.getRfc8040UriFromIif(LogicalDatastoreType.CONFIGURATION,
                InstanceIdentifier.create(OrgOpenroadmDevice.class).child(Info.class), ROADMAA_NODEID, false);
        assertEquals(ROADMAA_INFO, uri);
    }

    @Test
    public void test2() throws Exception {
        InstanceIdentifier<SharedRiskGroup> srgIID = InstanceIdentifier.create(OrgOpenroadmDevice.class)
                .child(SharedRiskGroup.class, new SharedRiskGroupKey(1));
        TestRestconfHttpClient restconfClient = new TestRestconfHttpClient("http://localhost:8181/", false, AuthMethod.BASIC,"","");
        String uri;
        uri = restconfClient.getRfc8040UriFromIif(LogicalDatastoreType.CONFIGURATION,
                srgIID, ROADMAA_NODEID, false);
        LOG.info(uri);
        assertEquals(ROADMAA_SRG_1, uri);
    }
//    @Test
//    public void test3() {
//        InstanceIdentifier<TerminationPoint> iiTp = InstanceIdentifier.builder(Networks.class)
//                .child(Network.class, new NetworkKey(new NetworkId("network-topology")))
//                .child(Node.class, new NodeKey(new NodeId(srcNode)))
//                .augmentation(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
//                    .Node1.class)
//                .child(TerminationPoint.class, new TerminationPointKey(new TpId(srcTp)))
//                .build();
//    }

    private void testTopology() throws Exception {
        InstanceIdentifier<Topology> iif = null;
        TestRestconfHttpClient restconfClient = new TestRestconfHttpClient("http://localhost:8181/", false, AuthMethod.BASIC,"","");
        @NonNull

        Optional<Topology> output = restconfClient.read(LogicalDatastoreType.CONFIGURATION,iif).get();
        Topology topo = output.get();
    }

    private class TestRestconfHttpClient extends RestconfHttpClient {

        TestRestconfHttpClient(String base, boolean trustAllCerts, AuthMethod authMethod,
                String username, String password) throws Exception {
            super(base, trustAllCerts,authMethod, username, password);
            // TODO Auto-generated constructor stub
        }

        @Override
        public <T extends DataObject> String getRfc8040UriFromIif(LogicalDatastoreType storage,
                InstanceIdentifier<T> instanceIdentifier, String nodeId, boolean isRpc) throws ClassNotFoundException,
                NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
            // TODO Auto-generated method stub
            return super.getRfc8040UriFromIif(storage, instanceIdentifier, nodeId, isRpc);
        }
    }

    @Test
    public void testIIF2() throws Exception {
        String connectionNumber = "1";
        InstanceIdentifier<OduConnection> iif =
                InstanceIdentifier.create(OrgOpenroadmDevice.class)
                    .child(OduConnection.class, new OduConnectionKey(connectionNumber));
        TestRestconfHttpClient restconfClient =
                new TestRestconfHttpClient("http://localhost:8181/", false, AuthMethod.BASIC, "", "");
        LOG.info("odu uri={}",
            restconfClient.getRfc8040UriFromIif(LogicalDatastoreType.CONFIGURATION, iif, "abc", false));
    }
}
