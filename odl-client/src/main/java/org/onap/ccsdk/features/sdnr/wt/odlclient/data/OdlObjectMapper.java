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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jdt.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.deserializer.CustomOdlDeserializer;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer.DateAndTimeSerializer;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.NodeIdType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DateAndTime;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdlObjectMapper extends ObjectMapper implements ClassFinder{

    private static final Logger LOG = LoggerFactory.getLogger(OdlObjectMapper.class);
    private static final long serialVersionUID = 1L;
    private final YangToolsBuilderAnnotationIntrospector introspector;
    public OdlObjectMapper() {
        super();
        Bundle bundle = FrameworkUtil.getBundle(OdlObjectMapper.class);
        BundleContext context = bundle != null ? bundle.getBundleContext() : null;
        this.introspector = new YangToolsBuilderAnnotationIntrospector(context);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        setSerializationInclusion(Include.NON_NULL);
        setAnnotationIntrospector(this.introspector);

        SimpleModule customSerializerModule = new SimpleModule();
        customSerializerModule.addSerializer(DateAndTime.class, new DateAndTimeSerializer());
        //        customSerializerModule.addSerializer(EquipmentEntity.class, new CustomChoiceSerializer());
        customSerializerModule.setDeserializerModifier(new CustomOdlDeserializer(this));
        customSerializerModule.addKeyDeserializer(DataObject.class, new KeyDeserializer() {

            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
                if (key.contains(":")) {
                    String[] hlp = key.split(":");
                    key = hlp[hlp.length - 1];
                }
                LOG.trace("using key=", key);
                return ctxt.getAttribute(key);
            }
        });
        this.registerModule(customSerializerModule);
        this.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return super.writeValueAsString(value);
    }

    /**
     * Get Builder object for yang tools interface.
     *
     * @param <T> yang-tools base datatype
     * @param clazz class with interface.
     * @return builder for interface or null if not existing
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public @Nullable <T extends DataObject> Builder<T> getBuilder(Class<T> clazz) {
        String builder = clazz.getName() + "Builder";
        try {
            Class<?> clazzBuilder = this.introspector.findClass(builder);
            return (Builder<T>) clazzBuilder.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOG.debug("Problem ", e);
            return null;
        }
    }

    /**
     * Callback for handling mapping failures.
     *
     * @return
     */
    public int getMappingFailures() {
        return 0;
    }

    public static class DateAndTimeBuilder {

        private final String value;

        public DateAndTimeBuilder(String value) {
            this.value = value;
        }

        public DateAndTime build() {
            return new DateAndTime(value);
        }

    }

    public static class CustomNodeIdTypeDeserializer extends JsonDeserializer<NodeIdType> {

        @Override
        public NodeIdType deserialize(JsonParser parser, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            return new NodeIdType(parser.getValueAsString());
        }

    }


    public <T> T readValue(String input, Class<T> clazz, String key)
            throws JsonParseException, JsonMappingException, IOException {
        JSONObject obj = new JSONObject(input);
        Object inner = obj.get(key);
        if (inner instanceof JSONArray) {
            input = ((JSONArray) inner).getJSONObject(0).toString();
        } else {
            input = ((JSONObject) inner).toString();
        }
        return this.readValue(input, clazz);
    }


    @Override
    public Class<?> findClass(String name, Class<?> clazz) throws ClassNotFoundException {
        return this.introspector.findClass(name, clazz);
    }
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException{
        return this.introspector.findClass(name);
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
    public @Nullable <T> Builder<T> getBuilder(Class<T> clazz, T value) {
        String builder = clazz.getName() + "Builder";
        try {
            Class<?> clazzBuilder = this.introspector.findClass(builder);
            return (Builder<T>) clazzBuilder.getDeclaredConstructor(clazz).newInstance(value);
        } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            // TODO Auto-generated catch block

        }
        return null;
    }
}
