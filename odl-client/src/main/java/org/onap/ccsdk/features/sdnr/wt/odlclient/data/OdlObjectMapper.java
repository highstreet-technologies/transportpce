/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.eclipse.jdt.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.NodeIdType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DateAndTime;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.TypeObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdlObjectMapper extends ObjectMapper {

    private static final Logger LOG = LoggerFactory.getLogger(OdlObjectMapper.class);
    private static final long serialVersionUID = 1L;
    private static final String TYPEOBJECT_INSTANCE_METHOD = "getDefaultInstance";
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
        customSerializerModule.addSerializer(DateAndTime.class, new CustomDateAndTimeSerializer());
        //        customSerializerModule.addSerializer(EquipmentEntity.class, new CustomChoiceSerializer());
        customSerializerModule.setDeserializerModifier(new CustomOdlDeserializer());
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

    public static class CustomOdlDeserializer extends BeanDeserializerModifier {
        @Override
        public JsonDeserializer<Enum<?>> modifyEnumDeserializer(DeserializationConfig config, final JavaType type,
                BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
            return new JsonDeserializer<Enum<?>>() {

                @SuppressWarnings("unchecked")
                @Override
                public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                    Class<?> clazz = type.getRawClass();

                    try {
                        LOG.debug("try to deserialize '{}' with class {}",jp.getValueAsString(),clazz.getName());
                        Method method = clazz.getDeclaredMethod("forName", String.class);
                        Enum<?> res = ((Optional<Enum<?>>) method.invoke(null, jp.getValueAsString())).get();
                        return res;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException | NoSuchElementException | SecurityException e) {
                        LOG.warn("problem deserializing enum for {} with value {}: {}", clazz.getName(),
                                jp.getValueAsString(), e);
                    }
                    throw new IOException(
                            "unable to parse enum (" + type.getRawClass() + ")for value " + jp.getValueAsString());
                }
            };

        }

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
                JsonDeserializer<?> deserializer) {
            final JavaType type = beanDesc.getType();
            final JsonDeserializer<?> deser = super.modifyDeserializer(config, beanDesc, deserializer);
            if (!implementsInterface(type.getRawClass(), TypeObject.class)) {
                //if (!implementsInterface(type.getRawClass(), ChoiceIn.class)) {
                    return deser;
                //}


            }


            return new JsonDeserializer<TypeObject>() {

                @Override
                public TypeObject deserialize(JsonParser parser, DeserializationContext ctxt)
                        throws IOException, JsonProcessingException {

                    Class<?> clazz = type.getRawClass();
                    final String arg = parser.getValueAsString();
                    try {

                        if (hasClassDeclaredMethod(clazz, TYPEOBJECT_INSTANCE_METHOD)) {
                            Method method = clazz.getDeclaredMethod(TYPEOBJECT_INSTANCE_METHOD, String.class);
                            TypeObject res = (TypeObject) method.invoke(null, arg);
                            return res;
                        } else {
                            //try to find builder with getDefaultInstance method
                            try {
                                Class<?> builderClazz = findBuilderClass(ctxt,clazz);
                                if (hasClassDeclaredMethod(builderClazz, TYPEOBJECT_INSTANCE_METHOD)) {
                                    Method method = builderClazz.getDeclaredMethod(TYPEOBJECT_INSTANCE_METHOD, String.class);
                                    TypeObject res = (TypeObject) method.invoke(null, arg);
                                    return res;
                                }
                            } catch (ClassNotFoundException e) {

                            }


                            // find constructor argument types
                            List<Class<?>> ctypes = getConstructorParameterTypes(clazz, String.class);
                            for (Class<?> ctype : ctypes) {
                                if (ctype.equals(String.class)) {
                                    return (TypeObject) clazz.getConstructor(ctype).newInstance(arg);
                                } else if (hasClassDeclaredMethod(ctype, TYPEOBJECT_INSTANCE_METHOD)) {
                                    Method method = ctype.getDeclaredMethod(TYPEOBJECT_INSTANCE_METHOD, String.class);
                                    return (TypeObject) clazz.getConstructor(ctype)
                                            .newInstance(method.invoke(null, arg));
                                } else {
                                    // TODO: recursive instantiation down to string constructor or
                                    // getDefaultInstance method
                                }
                            }

                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException | NoSuchElementException | SecurityException
                            | InstantiationException e) {
                        LOG.warn("problem deserializing {} with value {}: {}", clazz.getName(), arg, e);
                    }
                    return (TypeObject) deser.deserialize(parser, ctxt);
                }

                private Class<?> findBuilderClass(DeserializationContext ctxt,Class<?> clazz) throws ClassNotFoundException{
                    return ctxt.findClass(clazz.getName() + "Builder");
                }

            };
        }

    }

    //    public static class CustomChoiceSerializer extends StdSerializer<ChoiceIn>{
    //
    //        /**
    //         *
    //         */
    //        private static final long serialVersionUID = 1L;
    //
    //        protected CustomChoiceSerializer(Class<ChoiceIn> t) {
    //            super(t);
    //            // TODO Auto-generated constructor stub
    //        }
    //
    //        public CustomChoiceSerializer() {
    //           this(null);
    //        }
    //
    //        @Override
    //        public void serialize(ChoiceIn value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    ////            gen.writeStartObject();
    ////            gen.writeNumberField("id", 4);
    ////            gen.writeStringField("itemName", "name");
    ////            gen.writeNumberField("owner", 55);
    ////            gen.writeEndObject();
    //            gen.writeString("innerchoice");
    //        }
    //
    //    }
    public static class CustomDateAndTimeSerializer extends StdSerializer<DateAndTime> {

        private static final long serialVersionUID = 1L;

        public CustomDateAndTimeSerializer() {
            this(null);
        }

        protected CustomDateAndTimeSerializer(Class<DateAndTime> clazz) {
            super(clazz);
        }

        @Override
        public void serialize(DateAndTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getValue());
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

    public static boolean implementsInterface(Class<?> clz, Class<?> ifToImplement) {
        Class<?>[] ifs = clz.getInterfaces();
        for (Class<?> iff : ifs) {
            if (iff.equals(ifToImplement)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasClassDeclaredMethod(Class<?> clazz, String name) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static List<Class<?>> getConstructorParameterTypes(Class<?> clazz, Class<?> prefer) {

        Constructor<?>[] constructors = clazz.getConstructors();
        List<Class<?>> res = new ArrayList<>();
        for (Constructor<?> c : constructors) {
            Class<?>[] ptypes = c.getParameterTypes();
            if (ptypes.length == 1) {
                res.add(ptypes[0]);
            }

            if (prefer != null && ptypes.length == 1 && ptypes[0].equals(prefer)) {
                return Arrays.asList(prefer);
            }
        }
        return res;
    }
}
