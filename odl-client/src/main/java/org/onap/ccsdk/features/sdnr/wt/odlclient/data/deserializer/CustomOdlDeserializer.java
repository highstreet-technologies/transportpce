/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.ClassFinder;
import org.opendaylight.yangtools.yang.binding.BaseIdentity;
import org.opendaylight.yangtools.yang.binding.ScalarTypeObject;
import org.opendaylight.yangtools.yang.binding.TypeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomOdlDeserializer extends BeanDeserializerModifier {


    private static final Logger LOG = LoggerFactory.getLogger(CustomOdlDeserializer.class);
    private final ClassFinder clsFinder;

    public CustomOdlDeserializer(ClassFinder clsFinder) {
        this.clsFinder = clsFinder;
    }

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
                    LOG.warn("problem deserializing enum for {} with value {}: ", clazz.getName(),
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
        final Class<?> rawClass = type.getRawClass();

        final JsonDeserializer<?> deser = super.modifyDeserializer(config, beanDesc, deserializer);
        if (implementsInterface(rawClass, TypeObject.class)) {
            return new TypeObjectJsonDeserializer<TypeObject>(type, deser);
        }
        if (implementsInterface(rawClass, ScalarTypeObject.class)) {
            return new TypeObjectJsonDeserializer<ScalarTypeObject>(type, deser);
        }
        if (implementsInterface(rawClass, Class.class)) {
            return new BaseIdentityJsonDeserializer<BaseIdentity>(deser, this.clsFinder);
        }
        if (rawClass.equals(Class.class)) {
            return new ClassJsonDeserializer(rawClass, this.clsFinder);
        }

        return deser;
    }

    public static boolean implementsInterface(Class<?> clz, Class<?> ifToImplement) {
        return ifToImplement.isAssignableFrom(clz);
    }

}
