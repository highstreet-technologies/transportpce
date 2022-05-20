/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 * All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.serialize;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.YangToolsMapperHelper;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.R100G;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.R107G;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.R111G;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.R200G;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.EthernetCsmacd;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.Ip;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.MediaChannelTrailTerminationPoint;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.NetworkMediaChannelConnectionTerminationPoint;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpenROADMOpticalMultiplex;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalChannel;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalTransport;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OtnOdu;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OtnOtu;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU01;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU12;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU13;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU23;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU2Ts;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU3Ts;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODTU4TsAllocated;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU0;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU2;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU2e;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU3;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU4;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODUCTP;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODUTTP;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODUTTPCTP;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODUflexCbr;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODUflexGfp;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTU0;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTU1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTU2;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTU2e;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTU3;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTU4;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OTUflex;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OdtuTypeIdentity;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OduFunctionIdentity;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OduRateIdentity;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.OtuRateIdentity;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.PayloadTypeDef;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.Unallocated;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If100GE;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If100GEODU4;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If10GE;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If10GEODU2;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If10GEODU2e;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If1GE;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If1GEODU0;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If40GE;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If40GEODU3;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCH;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCHOTU1ODU1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCHOTU2EODU2E;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCHOTU2ODU2;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCHOTU3ODU3;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCHOTU4ODU4;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOMS;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTS;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTU1ODU1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTU2ODU2;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTU2eODU2e;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTU3ODU3;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTU4ODU4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassDeserializer extends FromStringDeserializer<Class<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassDeserializer.class);
    private static final long serialVersionUID = 1L;
    private static final Map<String, Class<?>> exceptions = initExceptions();

    public ClassDeserializer(Class<?> vc) {
        super(vc);
    }

    private static Map<String, Class<?>> initExceptions() {
        final Map<String, Class<?>> map = new HashMap<>();
        map.put("if-10GE", If10GE.class);
        map.put("if-100GE", If100GE.class);
        map.put("ethernetCsmacd", EthernetCsmacd.class);
        map.put("opticalTransport", OpticalTransport.class);
        map.put("openROADMOpticalMultiplex", OpenROADMOpticalMultiplex.class);
        map.put("otnOdu", OtnOdu.class);
        map.put("opticalChannel", OpticalChannel.class);
        map.put("mediaChannelTrailTerminationPoint", MediaChannelTrailTerminationPoint.class);
        map.put("networkMediaChannelConnectionTerminationPoint", NetworkMediaChannelConnectionTerminationPoint.class);
        map.put("ODTU01",ODTU01.class);
        map.put("ODTU12",ODTU12.class);
        map.put("ODTU13",ODTU13.class);
        map.put("ODTU23",ODTU23.class);
        map.put("ODTU2Ts",ODTU2Ts.class);
        map.put("ODTU3Ts",ODTU3Ts.class);
        map.put("ODTU4TsAllocated",ODTU4TsAllocated.class);
        map.put("OdtuTypeIdentity",OdtuTypeIdentity.class);
        map.put("ODU0",ODU0.class);
        map.put("ODU1",ODU1.class);
        map.put("ODU2e",ODU2e.class);
        map.put("ODU2",ODU2.class);
        map.put("ODU3",ODU3.class);
        map.put("ODU4",ODU4.class);
        map.put("ODUCTP",ODUCTP.class);
        map.put("ODUflexCbr",ODUflexCbr.class);
        map.put("ODUflexGfp",ODUflexGfp.class);
        map.put("OduFunctionIdentity",OduFunctionIdentity.class);
        map.put("OduRateIdentity",OduRateIdentity.class);
        map.put("ODUTTPCTP",ODUTTPCTP.class);
        map.put("ODUTTP",ODUTTP.class);
        map.put("OTU0",OTU0.class);
        map.put("OTU1",OTU1.class);
        map.put("OTU2e",OTU2e.class);
        map.put("OTU2",OTU2.class);
        map.put("OTU3",OTU3.class);
        map.put("OTU4",OTU4.class);
        map.put("OTUflex",OTUflex.class);
        map.put("OtuRateIdentity",OtuRateIdentity.class);
        map.put("PayloadTypeDef",PayloadTypeDef.class);
        map.put("Unallocated",Unallocated.class);

        map.put("if-OTS", IfOTS.class);
        map.put("if-OMS", IfOMS.class);
        map.put("if-OCH", IfOCH.class);
        map.put("EthernetCsmacd", EthernetCsmacd.class);
        map.put("Ip", Ip.class);
        map.put("MediaChannelTrailTerminationPoint", MediaChannelTrailTerminationPoint.class);
        map.put("NetworkMediaChannelConnectionTerminationPoint", NetworkMediaChannelConnectionTerminationPoint.class);
        map.put("OpticalChannel", OpticalChannel.class);
        map.put("OpticalTransport", OpticalTransport.class);
        map.put("OtnOdu", OtnOdu.class);
        map.put("OtnOtu", OtnOtu.class);
        map.put("OpenROADMOpticalMultiplex", OpenROADMOpticalMultiplex.class);
        //org-openroadm-port-types@2018-10-19
        map.put("If100GE", If100GE.class);
        map.put("IfOMS", IfOMS.class);
        map.put("IfOTS", IfOTS.class);
        map.put("IfOCH", IfOCH.class);
        map.put("If1GE", If1GE.class);
        map.put("If10GE", If10GE.class);
        map.put("If40GE", If40GE.class);
        map.put("IfOCHOTU1ODU1", IfOCHOTU1ODU1.class);
        map.put("IfOCHOTU2ODU2", IfOCHOTU2ODU2.class);
        map.put("IfOCHOTU2EODU2E", IfOCHOTU2EODU2E.class);
        map.put("IfOCHOTU3ODU3", IfOCHOTU3ODU3.class);
        map.put("IfOCHOTU4ODU4", IfOCHOTU4ODU4.class);
        map.put("IfOTU4ODU4", IfOTU4ODU4.class);
        map.put("IfOTU1ODU1", IfOTU1ODU1.class);
        map.put("IfOTU2ODU2", IfOTU2ODU2.class);
        map.put("IfOTU2eODU2e", IfOTU2eODU2e.class);
        map.put("IfOTU3ODU3", IfOTU3ODU3.class);
        map.put("If1GEODU0", If1GEODU0.class);
        map.put("If10GEODU2", If10GEODU2.class);
        map.put("If10GEODU2e", If10GEODU2e.class);
        map.put("If40GEODU3", If40GEODU3.class);
        map.put("If100GEODU4", If100GEODU4.class);
        //org-openroadm-common-types@2018-10-19
        map.put("R200G", R200G.class);
        map.put("R100G", R100G.class);
        map.put(" R107G", R107G.class);
        map.put("R111G", R111G.class);
        return map;
    }

    @Override
    protected Class<?> _deserialize(String value, DeserializationContext ctxt) throws IOException {
        try {
            int idx = value.lastIndexOf(":");
            if (idx > 0) {
                value = value.substring(idx + 1);
            }
            if (exceptions.containsKey(value)) {
                return exceptions.get(value);
            }
            value = this.normalizeClassName(value);
            if (exceptions.containsKey(value)) {
                return exceptions.get(value);
            }
            return YangToolsMapperHelper.findClass(value);
        } catch (ClassNotFoundException e) {
            throw new IOException("Can not find class " + value, e);
        }
    }

    private String normalizeClassName(final String clsName) {
        String value = clsName.substring(0, 1).toUpperCase() + clsName.substring(1);
        value = value.replace("-", "");
        LOG.debug("normalize class name from {} to {}", clsName, value);
        return value;
    }
}
