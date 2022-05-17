/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.OpendaylightClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestStartup {

    private static final Logger LOG = LoggerFactory.getLogger(TestStartup.class);
    private static final String NODEID = "roadmaa";
    private static final String ODL_USERNAME = "admin";
    private static final String ODLPASSWORD = "admin";

    @Test
    public void test() throws Exception {
        OpendaylightClient odlClient = new OpendaylightClient("http://sdnr:8181",
                "ws://sdnr:8181/websocket", AuthMethod.BASIC, ODL_USERNAME, ODLPASSWORD);
        LOG.info("device is present={}", odlClient.isDevicePresent(NODEID));
        sleep(45000);
        odlClient.close();
        LOG.info("test finished");
    }

    private void sleep(int sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            LOG.warn("thread interrupted");
        }

    }
}
