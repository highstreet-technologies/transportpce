/*
 * Copyright © 2026 Not Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.lighty.controllers.tpce;

import static org.junit.Assert.assertNotNull;

import io.lighty.controllers.tpce.utils.TPCEUtils;
import io.lighty.core.controller.api.LightyController;
import io.lighty.core.controller.impl.LightyControllerBuilder;
import io.lighty.core.controller.impl.util.ControllerConfigUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.transportpce.pce.gnpy.consumer.GnpyConsumer;
import org.opendaylight.transportpce.pce.gnpy.consumer.GnpyConsumerImpl;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.Responses;
import org.opendaylight.yang.gen.v1.urn.gnpy.api.rev250613.Api;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnpyTest {

    private static final Logger LOG = LoggerFactory.getLogger(GnpyTest.class);
    private static final String GNPY_URL = System.getProperty("gnpy.url", "http://localhost:8080");
    private static final String GNPY_USERNAME = System.getProperty("gnpy.username", "gnpy");
    private static final String GNPY_PASSWORD = System.getProperty("gnpy.password", "gnpy");
    private static final Path GNPY_REQUEST_JSON_FILE =
            Path.of("src", "test", "resources", "gnpy", "gnpy_path_request.json");

    private static LightyController lightyController;
    private static GnpyConsumer gnpyConsumer;
    private static JsonStringConverter<Api> requestConverter;

    @BeforeClass
    public static void startLightyController() throws Exception {
        lightyController = new LightyControllerBuilder()
                .from(ControllerConfigUtils.getDefaultSingleNodeConfiguration(TPCEUtils.getYangModels()))
                .build();
        lightyController.start().get();
        BindingDOMCodecServices codecServices =
                lightyController.getServices().getAdapterContext().currentSerializer();
        gnpyConsumer = new GnpyConsumerImpl(GNPY_URL, GNPY_USERNAME, GNPY_PASSWORD, codecServices);
        requestConverter = new JsonStringConverter<>(codecServices);
    }

    @AfterClass
    public static void stopLightyController() throws Exception {
        if (lightyController != null) {
            lightyController.shutdown().get();
        }
    }

    @Test
    public void isAvailableTest() {
        assumeGnpyServerAvailable();
    }

    @Test
    public void computePathsTest() throws Exception {
        assumeGnpyServerAvailable();
        Api request = requestConverter.createDataObjectFromJsonString(
                YangInstanceIdentifier.of(Api.QNAME),
                Files.readString(GNPY_REQUEST_JSON_FILE),
                JSONCodecFactorySupplier.DRAFT_LHOTKA_NETMOD_YANG_JSON_02);
        Responses result = gnpyConsumer.computePaths(request);
        LOG.info("gnpy path computation result: {}", result);
        assertNotNull("gnpy should return a path computation result", result);
    }

    private static void assumeGnpyServerAvailable() {
        boolean available = gnpyConsumer.isAvailable();
        LOG.info("gnpy server at {} available: {}", GNPY_URL, available);
        Assume.assumeTrue("no gnpy server available at " + GNPY_URL
                + " (start the official gnpy-api locally or set -Dgnpy.url=...)", available);
    }
}
