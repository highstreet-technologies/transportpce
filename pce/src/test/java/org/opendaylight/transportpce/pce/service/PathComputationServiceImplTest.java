/*
 * Copyright © 2020 Orange Labs, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.pce.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opendaylight.transportpce.common.network.NetworkTransactionService;
import org.opendaylight.transportpce.pce.utils.PceTestData;
import org.opendaylight.transportpce.test.AbstractTest;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.AccumulatedMetricObject;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.PathBandwidth;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.Response;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.ResponseBuilder;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.ResponseKey;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.path.property.PathMetric;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.path.property.PathMetricBuilder;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.response.NoPathBuilder;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.response.PathPropertiesBuilder;
import org.opendaylight.yangtools.yang.common.Decimal64;

public class PathComputationServiceImplTest extends AbstractTest {

    private PathComputationServiceImpl pathComputationServiceImpl;
    private static NetworkTransactionService networkTransactionService = null;

    @BeforeEach
    void setUp() {
        networkTransactionService = mock(NetworkTransactionService.class);
        pathComputationServiceImpl = new PathComputationServiceImpl(
                networkTransactionService,
                this.getNotificationPublishService(), null, null);
    }

    @Test
    void pathComputationRequestTest() {
        pathComputationServiceImpl.generateGnpyResponse(null,"path");
        assertNotNull(
            pathComputationServiceImpl.pathComputationRequest(PceTestData.getPCE_simpletopology_test1_request()));
    }

    @Test
    void testPathComputationRequestNoPath() {
        Response response = new ResponseBuilder()
                .withKey(new ResponseKey("responseId"))
                .setNoPath(new NoPathBuilder().setNoPath(
                    org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.NOPATH.VALUE).build())
                .build();

        pathComputationServiceImpl.generateGnpyResponse(response,"path");
        assertNotNull(pathComputationServiceImpl.pathComputationRequest(PceTestData.getPCE_test3_request_54()));
    }

    @Test
    void testPathComputationRequestPathCase() {
        PathMetric pathMetric = new PathMetricBuilder()
                .setAccumulativeValue(new AccumulatedMetricObject.AccumulativeValue(Decimal64.valueOf("21")))
                .setMetricType(PathBandwidth.VALUE).build();
        Response response = new ResponseBuilder()
                .withKey(new ResponseKey("responseId"))
                .setPathProperties(new PathPropertiesBuilder().setPathMetric(Map.of(pathMetric.key(),pathMetric))
                .build()).build();

        pathComputationServiceImpl.generateGnpyResponse(response,"path");
        assertNotNull(pathComputationServiceImpl.pathComputationRequest(PceTestData.getPCE_test3_request_54()));
    }

    @Test
    void pathComputationRerouteRequestTest() {
        pathComputationServiceImpl.generateGnpyResponse(null,"path");
        assertNotNull(pathComputationServiceImpl.pathComputationRerouteRequest(PceTestData.getPCEReroute()));
    }
}
