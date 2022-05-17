/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.ClassFinder;
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

public class BaseIdentityJsonDeserializer<T> extends JsonDeserializer<Class<? extends T>> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseIdentityJsonDeserializer.class);
    private final JsonDeserializer<?> deser;
    private final ClassFinder clsFinder;
    private final Map<String, Class<?>> identityMap;


    public BaseIdentityJsonDeserializer(final JsonDeserializer<?> deser, ClassFinder clsFinder) {
        this.deser = deser;
        this.clsFinder = clsFinder;
        this.identityMap = initIdentityMap();
    }

    private static Map<String, Class<?>> initIdentityMap() {
        Map<String, Class<?>> map = new HashMap<>();
        //org-openroadm-interfaces@2017-06-26
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends T> deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        LOG.debug("BaseIdentityDeserializer class for '{}'", parser.getValueAsString());
        String clazzToSearch = parser.getValueAsString();
        //if full classname => nothing to do
        if (clazzToSearch.startsWith("org.opendaylight.yang")) {
            // clazz from Elasticsearch is full qualified
            int lastDot = clazzToSearch.lastIndexOf(".");
            clazzToSearch = clazzToSearch.substring(lastDot + 1);
        } else {
            clazzToSearch = clazzToSearch.substring(0, 1).toUpperCase()
                    + clazzToSearch.substring(1).replace("-", "").replace(".", "");
        }
        Class<?> clazz;
        clazz = this.identityMap.get(clazzToSearch);
        if (clazz != null)
            return (Class<? extends T>) clazz;
        try {
            clazz = ctxt.findClass(clazzToSearch);
            if (clazz != null)
                return (Class<? extends T>) clazz;
        } catch (ClassNotFoundException e) {
            try {
                clazz = this.clsFinder.findClass(clazzToSearch);
                if (clazz != null)
                    return (Class<? extends T>) clazz;
            } catch (ClassNotFoundException e2) {
                LOG.warn("BaseIdentityDeserializer class not found for '{}({})'", clazzToSearch,
                        parser.getValueAsString(), e2);
            }
        }
        return (Class<? extends T>) deser.deserialize(parser, ctxt);
    }
}
