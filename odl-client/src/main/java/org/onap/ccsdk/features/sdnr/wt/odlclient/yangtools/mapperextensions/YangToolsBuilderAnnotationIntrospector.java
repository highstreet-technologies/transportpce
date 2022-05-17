/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.mapperextensions;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.annotation.NonNull;
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

public class YangToolsBuilderAnnotationIntrospector extends JacksonAnnotationIntrospector {

    private static final Logger LOG = LoggerFactory.getLogger(YangToolsBuilderAnnotationIntrospector.class);
    private static final long serialVersionUID = 1L;
    private final BundleContext context;
    private final Map<Class<?>, String> customDeserializer;

    public YangToolsBuilderAnnotationIntrospector() {
        this(null);
    }

    public YangToolsBuilderAnnotationIntrospector(BundleContext context) {
        this.context = context;
        this.customDeserializer = new HashMap<>();
        this.customDeserializer.put(DateAndTime.class, DateAndTimeBuilder.class.getName());

    }

    public YangToolsBuilderAnnotationIntrospector(@NonNull Class<?> clazz, Class<?> builderClazz) {
        this();
        this.addDeserializer(clazz, builderClazz.getName());
    }
    public void addDeserializer(Class<?> clsToDeserialize, String builderClassName) {
        this.customDeserializer.put(clsToDeserialize, builderClassName);
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

    public Class<?> findClass(String name, BundleContext context) throws ClassNotFoundException {
        // Try to find in other bundles
        if (context != null) {
            //OSGi environment
            for (Bundle b : context.getBundles()) {
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
        } else {
            return Class.forName(name);
        }
        // not found in any bundle
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
