/*
 * Copyright © 2017 AT&T and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.transportpce.common.crossconnect;

import static org.opendaylight.transportpce.common.StringConstants.OPENROADM_DEVICE_VERSION_1_2_1;
import static org.opendaylight.transportpce.common.StringConstants.OPENROADM_DEVICE_VERSION_2_2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.opendaylight.transportpce.common.device.DeviceTransactionManager;
import org.opendaylight.transportpce.common.mapping.MappingUtils;
import org.opendaylight.transportpce.common.openroadminterfaces.OpenRoadmInterfaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossConnectImpl implements CrossConnect {

    private static final Logger LOG = LoggerFactory.getLogger(CrossConnectImpl.class);

    private final DeviceTransactionManager deviceTransactionManager;
    protected CrossConnect crossConnect;
    private final MappingUtils mappingUtils;
    private CrossConnectImpl121 crossConnectImpl121;
    private CrossConnectImpl22 crossConnectImpl22;

    public CrossConnectImpl(DeviceTransactionManager deviceTransactionManager, MappingUtils mappingUtils,
                            CrossConnectImpl121 crossConnectImpl121,
                            CrossConnectImpl22 crossConnectImpl22) {
        this.deviceTransactionManager = deviceTransactionManager;
        this.mappingUtils = mappingUtils;
        this.crossConnectImpl121 = crossConnectImpl121;
        this.crossConnectImpl22 = crossConnectImpl22;
        this.crossConnect = null;
    }

    public Optional<?> getCrossConnect(String nodeId, String connectionNumber) {
        String openRoadmVersion = mappingUtils.getOpenRoadmVersion(nodeId);
        if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_1_2_1)) {
            return crossConnectImpl121.getCrossConnect(nodeId,connectionNumber);
        }
        else if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_2_2)) {
            return crossConnectImpl22.getCrossConnect(nodeId,connectionNumber);
        }
        return null;
    }


    public Optional<String> postCrossConnect(String nodeId, Long waveNumber, String srcTp, String destTp) {
        String openRoadmVersion = mappingUtils.getOpenRoadmVersion(nodeId);
        LOG.info("Cross Connect post request received for node {} with version {}",nodeId,openRoadmVersion);
        if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_1_2_1)) {
            LOG.info("Device Version is 1.2.1");
            return crossConnectImpl121.postCrossConnect(nodeId, waveNumber, srcTp, destTp);
        }
        else if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_2_2)) {
            LOG.info("Device Version is 2.2");
            return crossConnectImpl22.postCrossConnect(nodeId, waveNumber, srcTp, destTp);
        }
        LOG.info("Device Version not found");
        return null;

    }


    public boolean deleteCrossConnect(String nodeId, String connectionNumber) {

        String openRoadmVersion = mappingUtils.getOpenRoadmVersion(nodeId);
        if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_1_2_1)) {
            return crossConnectImpl121.deleteCrossConnect(nodeId, connectionNumber);
        }
        else if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_2_2)) {
            return crossConnectImpl22.deleteCrossConnect(nodeId, connectionNumber);
        }
        return false;
    }

    public List<?> getConnectionPortTrail(String nodeId, Long waveNumber, String srcTp, String destTp)
            throws OpenRoadmInterfaceException {
        String openRoadmVersion = mappingUtils.getOpenRoadmVersion(nodeId);
        if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_1_2_1)) {
            return crossConnectImpl121.getConnectionPortTrail(nodeId, waveNumber, srcTp, destTp);
        }
        else if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_2_2)) {
            return crossConnectImpl22.getConnectionPortTrail(nodeId, waveNumber, srcTp, destTp);
        }
        return null;
    }

    public boolean setPowerLevel(String nodeId, Enum mode, BigDecimal powerValue,
                                 String connectionNumber) {
        String openRoadmVersion = mappingUtils.getOpenRoadmVersion(nodeId);
        if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_1_2_1)) {
            return crossConnectImpl121.setPowerLevel(nodeId,mode,powerValue,connectionNumber);
        }
        else if (openRoadmVersion.equals(OPENROADM_DEVICE_VERSION_2_2)) {
            return crossConnectImpl22.setPowerLevel(nodeId,mode,powerValue,connectionNumber);
        }
        return false;
    }
}
