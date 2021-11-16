/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlObjectMapper.DateAndTimeBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DateAndTime;
import org.opendaylight.yangtools.yang.common.Uint16;
import org.opendaylight.yangtools.yang.common.Uint32;
import org.opendaylight.yangtools.yang.common.Uint64;
import org.opendaylight.yangtools.yang.common.Uint8;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO check if another solution  would not be more indicated here
@SuppressFBWarnings(
    value = {"SE_BAD_FIELD"},
    justification =
      "This field is not Serializable but the class implements JacksonAnnotationIntrospector to delegate serialization."
      + "Thus instances of this class aren't serialized. SpotBugs does not recognize this.")
public class YangToolsBuilderAnnotationIntrospector extends JacksonAnnotationIntrospector {

    private static final Logger LOG = LoggerFactory.getLogger(YangToolsBuilderAnnotationIntrospector.class);
    private static final long serialVersionUID = 1L;
    private final BundleContext context;
    private final Map<Class<?>, String> customDeserializer;

    public YangToolsBuilderAnnotationIntrospector(BundleContext context) {
        this.context = context;
        this.customDeserializer = new HashMap<>();
        //this.customDeserializer.put(Credentials.class, LoginPasswordBuilder.class.getName());
        this.customDeserializer.put(DateAndTime.class, DateAndTimeBuilder.class.getName());
        //        this.customDeserializer.put(Info.class,InfoBuilder.class.getName());
        //        this.customDeserializer.put(GlobalConfig.class,GlobalConfigBuilder.class.getName());
        //        this.customDeserializer.put(Degree.class,DegreeBuilder.class.getName());
        //        this.customDeserializer.put(NetconfNode.class,NetconfNodeBuilder.class.getName());
        //        this.customDeserializer.put(SharedRiskGroup.class,SharedRiskGroupBuilder.class.getName());
        //        this.customDeserializer.put(McCapabilities.class,McCapabilitiesBuilder.class.getName());
        //        this.customDeserializer.put(CircuitPacks.class,CircuitPacksBuilder.class.getName());
        //        this.customDeserializer.put(ConnectionPorts.class,ConnectionPortsBuilder.class.getName());
        //        this.customDeserializer.put(org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.degree.
        //                CircuitPacks.class,org.onap.ccsdk.features.sdnr.wt.odlclient.data.builders.device.rev181019.
        //                degree.CircuitPacksBuilder.class.getName());
        //        this.customDeserializer.put(Interface.class,InterfaceBuilder.class.getName());
        //        this.customDeserializer.put(OtdrPort.class,OtdrPortBuilder.class.getName());
        //        this.customDeserializer.put(Ports.class,PortsBuilder.class.getName());
        //        this.customDeserializer.put(OduSwitchingPools.class,OduSwitchingPoolsBuilder.class.getName());
        //        this.customDeserializer.put(NonBlockingList.class,NonBlockingListBuilder.class.getName());
        //        this.customDeserializer.put(ConnectionMap.class,ConnectionMapBuilder.class.getName());
        //        this.customDeserializer.put(LineAmplifier.class,LineAmplifierBuilder.class.getName());
        //        this.customDeserializer.put(CircuitPack.class,CircuitPackBuilder.class.getName());
        //        this.customDeserializer.put(Xponder.class,XponderBuilder.class.getName());
        //        this.customDeserializer.put(XpdrPort.class,XpdrPortBuilder.class.getName());
        //        this.customDeserializer.put(Odu.class,OduBuilder.class.getName());
        //        this.customDeserializer.put(ParentOduAllocation.class,ParentOduAllocationBuilder.class.getName());
        //        this.customDeserializer.put(TxMsi.class,TxMsiBuilder.class.getName());
        //        this.customDeserializer.put(RxMsi.class,RxMsiBuilder.class.getName());
        //        this.customDeserializer.put(ExpMsi.class,ExpMsiBuilder.class.getName());
        //        this.customDeserializer.put(MaintTestsignal.class,MaintTestsignalBuilder.class.getName());
        //        this.customDeserializer.put(Tcm.class,TcmBuilder.class.getName());
        //        this.customDeserializer.put(Otu.class,OtuBuilder.class.getName());


    }

    @Override
    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        try {
            String builder = null;
            if (this.customDeserializer.containsKey(ac.getRawType())) {
                builder = this.customDeserializer.get(ac.getRawType());
            } else {
                if (ac.getRawType().isInterface()) {
                    builder = ac.getName() + "Builder";
                }
            }
            if (builder != null) {
                LOG.trace("map {} with builder {}", ac.getName(), builder);
                Class<?> innerBuilder = findClass(builder);
                return innerBuilder;
            }
        } catch (ClassNotFoundException e) {
            LOG.trace("builder class not found for {}", ac.getName());
        }
        return super.findPOJOBuilder(ac);
    }

    @Override
    public Value findPOJOBuilderConfig(AnnotatedClass ac) {
        if (ac.hasAnnotation(JsonPOJOBuilder.class)) {
            return super.findPOJOBuilderConfig(ac);
        }
        return new JsonPOJOBuilder.Value("build", "set");
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, context);
    }

    public Class<?> findClass(String name, Class<?> clazz) throws ClassNotFoundException {
        Bundle bundle = FrameworkUtil.getBundle(clazz);
        BundleContext ctx = bundle != null ? bundle.getBundleContext() : null;
        return findClass(name, ctx);
    }

    // In a class finder, the expected behavior is not to catch an exception when a Bundle is a class is not found
    // but to try the next ones.
    @SuppressWarnings("EmptyBlock")
    public Class<?> findClass(String name, BundleContext bundleContext) throws ClassNotFoundException {
        // Try to find in other bundles
        //OSGi environment
        if (bundleContext == null) {
            return Class.forName(name);
        }
        for (Bundle b : bundleContext.getBundles()) {
            try {
                return b.loadClass(name);
            } catch (ClassNotFoundException e) {
            }
        }
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
        }
        throw new ClassNotFoundException("Can not find Class in OSGi context.");
    }

    @Override
    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1,
            AnnotatedMethod setter2) {
        Class<?> p1 = setter1.getRawParameterType(0);
        Class<?> p2 = setter2.getRawParameterType(0);
        AnnotatedMethod res = null;

        if (this.isAssignable(p1, p2, Map.class, List.class)) {
            res = p1.isAssignableFrom(List.class) ? setter1 : setter2;
        } else if (this.isAssignable(p1, p2, Uint64.class, BigInteger.class)) {
            res = setter1;
        } else if (this.isAssignable(p1, p2, Uint32.class, Long.class)) {
            res = setter1;
        } else if (this.isAssignable(p1, p2, Uint16.class, Integer.class)) {
            res = setter1;
        } else if (this.isAssignable(p1, p2, Uint8.class, Short.class)) {
            res = setter1;
        }
        if (res == null) {
            res = super.resolveSetterConflict(config, setter1, setter2);
        }
        LOG.debug("{} (m1={} <=> m2={} => result:{})", setter1.getName(), p1.getSimpleName(), p2.getSimpleName(),
                res.getRawParameterType(0).getSimpleName());

        return res;
    }

    private boolean isAssignable(Class<?> p1, Class<?> p2, Class<?> c1, Class<?> c2) {
        return ((p1.isAssignableFrom(c1) && p2.isAssignableFrom(c2))
                || (p2.isAssignableFrom(c1) && p1.isAssignableFrom(c2)));

    }



}
