/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
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
package org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.mapperextensions.YangToolsBuilderAnnotationIntrospector;
import org.onap.ccsdk.features.sdnr.wt.odlclient.yangtools.mapperextensions.YangToolsModule;
import org.opendaylight.yang.gen.v1.http.org.openroadm.resource.rev181019.resource.resource.resource.Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * YangToolsMapper is a specific Jackson mapper configuration for opendaylight yangtools serialization or
 * deserialization of DataObject to/from JSON TODO ChoiceIn and Credentials deserialization only for
 * LoginPasswordBuilder
 */
public class YangToolsMapper extends ObjectMapper {

    @SuppressWarnings("unused")
    private final Logger LOG = LoggerFactory.getLogger(YangToolsMapper.class);
    private final JacksonAnnotationIntrospector annotationIntrospector;
    private final YangToolsModule module;
    private final boolean useInnerType;
    private static final long serialVersionUID = 1L;
    private static final String regexJsonPropertyPrefix = "\\\"([^:^\\\"]+:([^\\\"]+))\\\"\\s*:\\s*\\\"?[^\\\"]";
    private static final String regexJsonProperty = "\\\"([^\\\"]+)\\\":";
    private static final Pattern patternJsonPropertyPrefix = Pattern.compile(regexJsonPropertyPrefix, Pattern.MULTILINE);
    private static final Pattern patternJsonProperty = Pattern.compile(regexJsonProperty, Pattern.MULTILINE);
     private static final Map<Class<?>, List<Class<?>>> autoAugmentationList;

    static {
        autoAugmentationList = new HashMap<>();
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface.class,
                Arrays.asList(
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.otu.interfaces.rev181019.Interface1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019.Interface1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1.class));
        autoAugmentationList.put(Interface.class, Arrays.asList(
                org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019.Interface1.class));
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.network.Node.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.network.rev200529.Node1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Node1.class));
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Network1.class,
                Arrays.asList(
                        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Network1.class));
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Link1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529.Link1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.network.topology.rev200529.Link1.class));
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.network.Node.class,
                Arrays.asList(
                        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Node1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Node1.class));
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.node.TerminationPoint.class,
                Arrays.asList(
                        org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.TerminationPoint1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529.TerminationPoint1.class));
        autoAugmentationList.put(
                org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.Protocols.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.Protocols1.class));
    }

    public YangToolsMapper(boolean useInnerType) {
        this(new YangToolsBuilderAnnotationIntrospector(), useInnerType);
    }

    public YangToolsMapper() {
        this(new YangToolsBuilderAnnotationIntrospector(), false);
    }

    protected YangToolsMapper(JacksonAnnotationIntrospector yangToolsBuilderAnnotationIntrospector,
            boolean useInnerType) {
        super();
        this.useInnerType = useInnerType;
        this.annotationIntrospector = yangToolsBuilderAnnotationIntrospector;
        this.module = new YangToolsModule(this);
        this.registerModule(this.module);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        this.setSerializationInclusion(Include.NON_NULL);
        this.enable(MapperFeature.USE_GETTERS_AS_SETTERS);
        this.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        this.setAnnotationIntrospector(this.annotationIntrospector);
    }



    private String cleanupNamespacesAndNamingStrategy(String content) {
        final Matcher matcher = patternJsonPropertyPrefix.matcher(content);
        while (matcher.find()) {
            content = content.replaceFirst(matcher.group(1), matcher.group(2));
        }
        final Matcher matcher2 = patternJsonProperty.matcher(content);
        while (matcher2.find()) {
            String prop = matcher2.group(1);
            content = content.replaceFirst(prop,prop.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase());
        }
        return content;
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return super.writeValueAsString(value);
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
        return this.readValue(content, valueType, false);
    }

    public <T> T readValue(String content, Class<T> valueType, boolean isLeafListItem)
            throws JsonMappingException, JsonProcessingException {
        content = this.cleanupNamespacesAndNamingStrategy(content);
        if (this.useInnerType) {
            JSONObject obj = new JSONObject(content);
            Object item = obj.get((String) obj.keys().next());
            if (isLeafListItem) {
                if (item instanceof JSONArray) {
                    item = ((JSONArray) item).length() > 0 ? ((JSONArray) item).get(0) : "{}";
                } else {
                    LOG.warn("try to read array leaf value for class {} but no array was returned with content: {}",
                            valueType, content);
                }
            }
            content = item.toString();
        }
        return this.readValueInner(content, valueType);
    }

    private <T> T readValueInner(String content, Class<T> valueType) throws JsonProcessingException {

        List<Class<?>> augs = autoAugmentationList.getOrDefault(valueType, null);
        if (augs != null) {
            Class<?>[] a = new Class<?>[augs.size()];
            try {
                return this.readValueInner(content, valueType, autoAugmentationList.get(valueType).toArray(a));
            } catch (IOException e) {
                LOG.warn("problem reading value");
                throw new JsonIOException(e.getCause());
            }
        }
        return super.readValue(content, valueType);
    }

    private <T> T readValueInner(String content, Class<T> valueType, Class<?>... augmentedTypes)
            throws IOException, JsonParseException, JsonMappingException {

        T value = super.readValue(content, valueType);

        if (augmentedTypes.length > 0) {
            org.opendaylight.yangtools.concepts.@Nullable Builder<T> builder = this.getBuilder(valueType, value);
            if (builder != null) {
                Method addAugmentationMethod = null;
                for (Method m : builder.getClass().getDeclaredMethods()) {
                    if ((m.getName() == "addAugmentation") && m.getParameterCount() == 1) {
                        addAugmentationMethod = m;
                        break;
                    }
                }
                if (addAugmentationMethod != null) {
                    for (Class<?> augmentedType : augmentedTypes) {
                        try {
                            addAugmentationMethod.invoke(builder, super.readValue(content, augmentedType));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | IOException e) {
                            LOG.warn("unable to add augmented type {} to basetype {} with content {}: ", augmentedType,
                                    valueType, content, e);
                        }
                    }
                    value = builder.build();
                } else {
                    LOG.warn("unable to add augmentations to type {}. No fn with this name found", builder.getClass());
                }
            } else {
                LOG.warn("no builder foun for type {}", valueType);
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
    public <T> org.opendaylight.yangtools.concepts.@Nullable Builder<T> getBuilder(Class<T> clazz, T value) {
        String builder = clazz.getName() + "Builder";
        try {
            Class<?> clazzBuilder = YangToolsMapperHelper.findClass(builder);
            return (org.opendaylight.yangtools.concepts.@Nullable Builder<T>) clazzBuilder.getDeclaredConstructor(clazz)
                    .newInstance(value);
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            // TODO Auto-generated catch block

        }
        return null;
    }
}
