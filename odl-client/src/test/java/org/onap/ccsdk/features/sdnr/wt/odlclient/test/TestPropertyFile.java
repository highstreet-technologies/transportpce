/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import static org.junit.Assert.assertEquals;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;

public class TestPropertyFile {

    private static final String TESTFILENAME = "test.properties";
    private static final String TEST_ODLURL = "http://10.20.40.55:8222";
    private static final String TEST_WSURL = "ws://10.20.40.55:8222/websocket";
    private static final String TEST_ODLUSER = "heribert";
    private static final String TEST_ODLPASSWORD = "hereyoumayseeyourad";
    private static final AuthMethod TEST_AUTHMETHOD = AuthMethod.BASIC;
    private static final boolean TEST_ENABLED = true;
    private static final CharSequence CONTENT1 = String.format(
            "%s=%s\n" + "%s=%s\n" + "%s=%s\n" + "%s=%s\n" + "%s=%s\n" + "%s=%s\n",
            RemoteOdlConfig.KEY_BASEURL, TEST_ODLURL, RemoteOdlConfig.KEY_WSURL, TEST_WSURL,
            RemoteOdlConfig.KEY_AUTHMETHOD, TEST_AUTHMETHOD.name(), RemoteOdlConfig.KEY_USERNAME,
            TEST_ODLUSER, RemoteOdlConfig.KEY_PASSWORD, TEST_ODLPASSWORD, RemoteOdlConfig.KEY_ENABLED,
            String.valueOf(TEST_ENABLED));

    @Test
    public void test() throws IOException {
        File file = new File(TESTFILENAME);
        if (file.exists()) {
            file.delete();
        }
        Files.asCharSink(file, StandardCharsets.UTF_8).write(CONTENT1);
        RemoteOdlConfig config = new RemoteOdlConfig(TESTFILENAME);

        assertEquals(TEST_ODLURL, config.getBaseUrl());
        assertEquals(TEST_WSURL, config.getWebsocketUrl());
        assertEquals(TEST_ODLUSER, config.getCredentialUsername());
        assertEquals(TEST_ODLPASSWORD, config.getCredentialPassword());
        assertEquals(TEST_AUTHMETHOD, config.getAuthenticationMethod());
        assertEquals(TEST_ENABLED, config.isEnabled());
        if (file.exists()) {
            file.delete();
        }

    }
}
