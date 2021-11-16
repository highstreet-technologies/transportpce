/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.JsonIOException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jdt.annotation.Nullable;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.deserializer.CustomOdlDeserializer;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer.DateAndTimeSerializer;
import org.opendaylight.yang.gen.v1.http.org.openroadm.resource.rev181019.resource.resource.resource.Interface;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DateAndTime;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OdlObjectMapperXml extends XmlMapper implements ClassFinder {

    private static final Logger LOG = LoggerFactory.getLogger(OdlObjectMapperXml.class);
    private static final long serialVersionUID = 1L;
    private static final String NORMALIZE_REGEX = "<[\\/]{0,1}([a-z]+[A-Z]+[^>]*)>";
    private static final Pattern NORMALIZE_PATTERN = Pattern.compile(NORMALIZE_REGEX, Pattern.MULTILINE);
    private static final String XMLNS_REGEX = "xmlns:([a-zA-Z0-9])=\"([^>]*)\">([^<]*)<\\/";
    private static final Pattern XMLNS_PATTERN = Pattern.compile(XMLNS_REGEX, Pattern.MULTILINE);

    private final boolean doNormalize;
    private final YangToolsBuilderAnnotationIntrospector introspector;
    private final Map<Class<?>,List<Class<?>>> autoAugmentationList;

    public OdlObjectMapperXml() {
        this(false);
    }

    public OdlObjectMapperXml(boolean doNormalize) {
        super();
        this.autoAugmentationList = initAutoAugmentationList();
        this.doNormalize = doNormalize;
        Bundle bundle = FrameworkUtil.getBundle(OdlObjectMapperXml.class);
        BundleContext context = bundle != null ? bundle.getBundleContext() : null;
        this.introspector = new YangToolsBuilderAnnotationIntrospector(context);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        enable(MapperFeature.USE_GETTERS_AS_SETTERS);
        setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        //setPropertyNamingStrategy(new YangToolsNamingStrategy());
        setSerializationInclusion(Include.NON_NULL);
        setAnnotationIntrospector(this.introspector);
        SimpleModule customSerializerModule = new SimpleModule();
        customSerializerModule.addSerializer(DateAndTime.class, new DateAndTimeSerializer());
        //        customSerializerModule.addSerializer(ChoiceIn.class, new CustomChoiceSerializer());
        customSerializerModule.setDeserializerModifier(new CustomOdlDeserializer(this));

        this.registerModule(customSerializerModule);
        this.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    }


    public static Map<Class<?>,List<Class<?>>> initAutoAugmentationList() {
        final Map<Class<?>,List<Class<?>>> map = new HashMap<>();
        map.put(org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface.class,
                Arrays.asList(
                    org.opendaylight.yang.gen.v1.http.org.openroadm.otn.otu.interfaces.rev181019.Interface1.class,
                    org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1.class,
                    org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019
                        .Interface1.class,
                    org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019
                        .Interface1.class));
        map.put(Interface.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019
                    .Interface1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226
                    .networks.network.Node.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.network.rev200529.Node1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Node1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Network1.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                    .Network1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                    .networks.network.Link.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Link1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529.Link1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.network.topology.rev200529.Link1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226
                    .networks.network.Node.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                            .Node1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Node1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                    .networks.network.node.TerminationPoint.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529
                            .TerminationPoint1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529
                            .TerminationPoint1.class));
        map.put(org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019
                    .org.openroadm.device.container.org.openroadm.device.Protocols.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.Protocols1.class)
                );
        return map;
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType)
            throws JsonProcessingException {
        if (this.doNormalize) {
            content = this.normalizeContent(content);
        }
        List<Class<?>> augs = this.autoAugmentationList.getOrDefault(valueType, null);
        if (augs != null) {
            Class<?>[] a1 = new Class<?>[augs.size()];
            try {
                return this.readValue(content, valueType, this.autoAugmentationList.get(valueType).toArray(a1));
            } catch (IOException e) {
                LOG.warn("problem reading value");
                throw new JsonIOException(e);
            }
        }
        return super.readValue(content, valueType);
    }

    public <T> T readValue(String content, Class<T> valueType, Class<?>... augmentedTypes)
            throws IOException, JsonParseException, JsonMappingException {
        if (this.doNormalize) {
            content = this.normalizeContent(content);
        }
        T value = super.readValue(content, valueType);

        if (augmentedTypes.length > 0) {
            org.opendaylight.yangtools.concepts.@Nullable Builder<T> builder = this.getBuilder(valueType, value);
            if (builder != null) {
                Method addAugmentationMethod = null;
                for (Method m : builder.getClass().getDeclaredMethods()) {
                    if (m.getName().equals("addAugmentation") && m.getParameterCount() == 2) {
                        addAugmentationMethod = m;
                        break;
                    }
                }
                if (addAugmentationMethod != null) {
                    for (Class<?> augmentedType : augmentedTypes) {
                        try {
                            addAugmentationMethod.invoke(builder,augmentedType, this.readValue(content, augmentedType));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | IOException e) {
                            LOG.warn("unable to add augmented type {} to basetype {} with content {}: ", augmentedType,
                                    valueType, content, e);
                        }
                    }
                    value = builder.build();
                }
                else {
                    LOG.warn("unable to add augmentations to type {}. No fn with this name found",builder.getClass());
                }
            }
            else {
                LOG.warn("no builder foun for type {}",valueType);
            }
        }
        return value;
    }

    /**
     * Get Builder object for yang tools interface.
     *
     * @param <T> yang-tools base datatype
     * @param clazz class with interface.
     * @return builder for interface or null if not existing
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public <T> org.opendaylight.yangtools.concepts.@Nullable Builder<T> getBuilder(Class<T> clazz, T value) {
        String builder = clazz.getName() + "Builder";
        try {
            Class<?> clazzBuilder = this.introspector.findClass(builder);
            return (org.opendaylight.yangtools.concepts.@Nullable Builder<T>)
                    clazzBuilder.getDeclaredConstructor(clazz).newInstance(value);
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            // TODO Auto-generated catch block

        }
        return null;
    }

    private String normalizeContent(String content) {
        LOG.debug("normalize content");
        LOG.trace("before={}", content);
        final Matcher matcher = NORMALIZE_PATTERN.matcher(content);
        String copy = content;
        String attr;
        KebabCaseStrategy converter = new KebabCaseStrategy();
        while (matcher.find()) {
            if (matcher.groupCount() > 0) {
                attr = matcher.group(1);
                copy = copy.replaceFirst(attr, converter.translate(attr));
            }
        }
        final Matcher xmlnsMatcher = XMLNS_PATTERN.matcher(content);
        while (xmlnsMatcher.find()) {
            if (xmlnsMatcher.groupCount() > 2) {
                attr = xmlnsMatcher.group(3);
                if (attr.startsWith(xmlnsMatcher.group(1) + ":")) {
                    copy = copy.replaceFirst(attr,attr.substring(xmlnsMatcher.group(1).length() + 1));
                }
            }
        }
        LOG.trace("after={}", copy);
        return copy;
    }

    @Override
    public Class<?> findClass(String name, Class<?> clazz) throws ClassNotFoundException {
        return this.introspector.findClass(name, clazz);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return this.introspector.findClass(name);
    }

}
