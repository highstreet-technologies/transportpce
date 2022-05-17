/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.ClassFinder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.EthernetCsmacd;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.MediaChannelTrailTerminationPoint;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.NetworkMediaChannelConnectionTerminationPoint;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpenROADMOpticalMultiplex;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalChannel;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalTransport;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OtnOdu;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.common.types.rev171215.ODU4;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If100GE;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.If10GE;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCH;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOMS;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassJsonDeserializer extends FromStringDeserializer<Class<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ClassJsonDeserializer.class);
    private static final long serialVersionUID = 1L;
    private static final Map<String,Class<?>> exceptions = initExceptions();
    private final ClassFinder clsFinder;
    public ClassJsonDeserializer(Class<?> vc, ClassFinder clsFinder) {
        super(vc);
        this.clsFinder = clsFinder;

    }

    private static Map<String, Class<?>> initExceptions() {
        final Map<String,Class<?>> map = new HashMap<>();
        map.put("if-10GE", If10GE.class);
        map.put("if-100GE", If100GE.class);
        map.put("ethernetCsmacd", EthernetCsmacd.class);
        map.put("opticalTransport", OpticalTransport.class);
        map.put("openROADMOpticalMultiplex", OpenROADMOpticalMultiplex.class);
        map.put("otnOdu", OtnOdu.class);
        map.put("opticalChannel", OpticalChannel.class);
        map.put("mediaChannelTrailTerminationPoint", MediaChannelTrailTerminationPoint.class);
        map.put("networkMediaChannelConnectionTerminationPoint", NetworkMediaChannelConnectionTerminationPoint.class);
        map.put("ODU4", ODU4.class);

        map.put("if-OTS", IfOTS.class);
        map.put("if-OMS", IfOMS.class);
        map.put("if-OCH", IfOCH.class);
        return map;
    }

    @Override
    protected Class<?> _deserialize(String value, DeserializationContext ctxt) throws IOException {
        try {
            if(exceptions.containsKey(value)) {
                return exceptions.get(value);
            }
            return this.clsFinder.findClass(this.normalizeClassName(value));
        } catch (ClassNotFoundException e) {
            throw new IOException("Can not find class "+value,e);
        }
    }
    private String normalizeClassName(final String clsName) {
        String value = clsName.substring(0,1).toUpperCase()+clsName.substring(1);
        value = value.replace("-", "");
        LOG.debug("normalize class name from {} to {}",clsName,value);
        return value;
    }
}
