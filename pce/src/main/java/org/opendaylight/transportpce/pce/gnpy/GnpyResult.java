/*
 * Copyright © 2018 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.transportpce.pce.gnpy;

import java.util.ArrayList;
import java.util.List;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.AccumulatedMetricObject;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.Responses;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.explicit.route.hop.subobject.type.NumUnnumHop;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.Response;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.path.property.PathMetric;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.path.route.object.PathRouteObjects;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.response.path.route.object.path.route.objects.PathRouteObject;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.node.types.rev210528.NodeIdType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.routing.constraints.rev221209.constraints.Include;
import org.opendaylight.yang.gen.v1.http.org.openroadm.routing.constraints.rev221209.constraints.IncludeBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.routing.constraints.rev221209.routing.constraints.HardConstraints;
import org.opendaylight.yang.gen.v1.http.org.openroadm.routing.constraints.rev221209.routing.constraints.HardConstraintsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for analyzing the result sent by GNPy.
 *
 * @author Ahmed Triki ( ahmed.triki@orange.com )
 *
 */

public class GnpyResult {

    private static final Logger LOG = LoggerFactory.getLogger(GnpyResult.class);
    private Response response = null;
    private List<String> ordNodeList = new ArrayList<>();

    public GnpyResult(Responses responses, GnpyTopoImpl gnpyTopo) throws GnpyException {
        this.ordNodeList = gnpyTopo.getElementsList();
        List<Response> responsesList = new ArrayList<>(responses.nonnullResponse().values());
        if (responsesList.isEmpty()) {
            throw new GnpyException("In GnpyResult: the response from GNpy is null!");
        }
        LOG.info("The response id is {}; ", responsesList.get(0).getResponseId());
        this.response = responsesList.get(0);
    }

    public boolean getPathFeasibility() {
        boolean isFeasible = false;
        if (response != null) {
            if (response.getNoPath() != null) {
                LOG.info("In GnpyResult: The path is not feasible ");
            } else if (response.getPathProperties() != null) {
                isFeasible = true;
                LOG.info("In GnpyResult: The path is feasible ");
            }
        }
        return isFeasible;
    }

    public List<PathRouteObjects> analyzeResult() {
        List<PathRouteObjects> pathRouteObjectList = null;
        if (response != null) {
            if (response.getNoPath() != null) {
                String noPathType = String.valueOf(response.getNoPath().getNoPath());
                LOG.info("GNPy: No path - {}", noPathType);
                if (response.getNoPath().getPathProperties() != null) {
                    List<PathMetric> pathMetricList = new ArrayList<>(response.getNoPath().getPathProperties()
                            .nonnullPathMetric().values());
                    LOG.info("GNPy : path is not feasible : {}", noPathType);
                    logPathMetrics(pathMetricList);
                }
            } else if (response.getPathProperties() != null) {
                LOG.info("GNPy : path is feasible");
                List<PathMetric> pathMetricList = new ArrayList<>(response.getPathProperties()
                        .nonnullPathMetric().values());
                // Path metrics
                logPathMetrics(pathMetricList);
                // Path route objects
                pathRouteObjectList = response.getPathProperties().getPathRouteObjects();
                LOG.info("in GnpyResult: finishing the computation of pathRouteObjectList");
            }
        }
        return pathRouteObjectList;
    }

    private void logPathMetrics(List<PathMetric> pathMetricList) {
        for (PathMetric pathMetric : pathMetricList) {
            String metricType = pathMetric.getMetricType().toString();
            AccumulatedMetricObject.AccumulativeValue accumulativeValue = pathMetric.getAccumulativeValue();
            LOG.info("Metric type {} // AccumulatriveValue {}", metricType, accumulativeValue);
        }
    }

    public HardConstraints computeHardConstraintsFromGnpyPath(List<PathRouteObjects> pathRouteObjectList) {
        HardConstraints hardConstraints = null;
        // Includes the list of nodes in the GNPy computed path as constraints
        // for the PCE
        List<NodeIdType> nodeIdList = new ArrayList<>();
        for (PathRouteObjects pathRouteObjects : pathRouteObjectList) {
            PathRouteObject pathRouteObject = pathRouteObjects.getPathRouteObject();
            if (pathRouteObject != null && pathRouteObject.getSubobjectType() instanceof NumUnnumHop) {
                NumUnnumHop numUnnumHop = (NumUnnumHop) pathRouteObject.getSubobjectType();
                String nodeId = numUnnumHop.getNumUnnumHop().getNodeId();
                if (nodeId != null && this.ordNodeList.contains(nodeId)) {
                    nodeIdList.add(new NodeIdType(nodeId));
                }
            }
        }

        Include include = new IncludeBuilder().setNodeId(nodeIdList).build();
        hardConstraints = new HardConstraintsBuilder().setInclude(include).build();
        return hardConstraints;
    }

    public Response getResponse() {
        return response;
    }
}
