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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeObjectJsonDeserializer<T> extends JsonDeserializer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(TypeObjectJsonDeserializer.class);
    private static final String TYPEOBJECT_INSTANCE_METHOD = "getDefaultInstance";
    private final JavaType type;
    private final JsonDeserializer<?> deser;

    public TypeObjectJsonDeserializer(JavaType type, JsonDeserializer<?> deser) {
        this.type = type;
        this.deser = deser;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        Class<?> clazz = type.getRawClass();
        final String arg = parser.getValueAsString();
        try {

            if (hasClassDeclaredMethod(clazz, TYPEOBJECT_INSTANCE_METHOD, String.class)) {
                Method method = clazz.getDeclaredMethod(TYPEOBJECT_INSTANCE_METHOD, String.class);
                T res = (T) method.invoke(null, arg);
                return res;
            } else {
                //try to find builder with getDefaultInstance method
                try {
                    Class<?> builderClazz = findBuilderClass(ctxt, clazz);
                    if (hasClassDeclaredMethod(builderClazz, TYPEOBJECT_INSTANCE_METHOD, String.class)) {
                        Method method = builderClazz.getDeclaredMethod(TYPEOBJECT_INSTANCE_METHOD, String.class);
                        T res = (T) method.invoke(null, arg);
                        return res;
                    }
                } catch (ClassNotFoundException e) {

                }


                // find constructor argument types
                List<Class<?>> ctypes = getConstructorParameterTypes(clazz, String.class);
                for (Class<?> ctype : ctypes) {
                    try {
                        if (ctype.equals(String.class)) {
                            return (T) clazz.getConstructor(ctype).newInstance(arg);
                        } else if (hasClassDeclaredMethod(ctype, TYPEOBJECT_INSTANCE_METHOD, String.class)) {
                            Method method = ctype.getDeclaredMethod(TYPEOBJECT_INSTANCE_METHOD, String.class);
                            return (T) clazz.getConstructor(ctype).newInstance(method.invoke(null, arg));
                        }
                    } catch (InvocationTargetException e) {
                        LOG.debug("unable to instantiate {} with value {}", ctype.getName(), arg);
                    }
                }
                // TODO: recursive instantiation down to string constructor or
                // e.g org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Host
                // Host->IpAddress->Ipv4Address/Ipv6Address
                ctypes = getConstructorParameterTypes(clazz, null);
                for (Class<?> ctype : ctypes) {
                    //ignore string(hasn't worked before) and self class
                    if (ctype.equals(String.class) || ctype.equals(clazz)) {
                        continue;
                    }
                    try {
                        Object value = recurseInstantiate(arg, ctype, 0, 4);
                        if (value != null) {
                            return (T) clazz.getConstructor(ctype).newInstance(value);
                        }
                    } catch (InvocationTargetException ex) {
                        LOG.debug("unable to instantiate {} with value {}", ctype.getName(), arg);
                    }

                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | NoSuchElementException | SecurityException | InstantiationException e) {
            LOG.warn("problem deserializing {} with value {}: {}", clazz.getName(), arg, e);
        }
        return (T) deser.deserialize(parser, ctxt);
    }

    private Object recurseInstantiate(String strValue, Class<?> clazz, int iteration, int maxIteration)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        if (iteration > maxIteration) {
            LOG.warn("max iteration {} in deserialization reached for class {}", iteration, clazz.getName());
            return null;
        }
        LOG.debug("iterate for typeobject class {} with value {}", clazz.getName(), strValue);
        List<Class<?>> ctypes = getConstructorParameterTypes(clazz, null);
        for (Class<?> ctype : ctypes) {
            //ignore self class
            if (ctype.equals(clazz)) {
                continue;
            }
            //constructor with string => instantiate
            //catch illegalargexception  for validity value checks
            if (ctype.equals(String.class)) {
                try {
                    return clazz.getConstructor(ctype).newInstance(strValue);
                } catch (InvocationTargetException ex) {
                    LOG.debug("unable to instantiate {} with value {}", ctype.getName(), strValue);
                }
            }
            //for each other object based constructor iterate
            Object value = recurseInstantiate(strValue, ctype, iteration + 1, maxIteration);
            if (value != null) {
                try {
                    return clazz.getConstructor(ctype).newInstance(value);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    LOG.debug("problem instantiating {} with value {}: ", ctype.getName(), strValue, e);
                }
            }
        }
        return null;
    }

    private Class<?> findBuilderClass(DeserializationContext ctxt, Class<?> clazz) throws ClassNotFoundException {
        return ctxt.findClass(clazz.getName() + "Builder");
    }

    private static boolean hasClassDeclaredMethod(Class<?> clazz, String name, Class<?> paramType) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(name) && m.getParameterCount() == 1) {
                if (paramType == null) {
                    return true;
                } else if (paramType.equals(m.getParameterTypes()[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * get list of constructor params for single arg constructors expect constructors of itself (e.g. Host(Host
     * host){...})
     *
     * @param clazz Class to detect constructors for
     * @param prefer prefered constructor argument. If exists return only this. optional/nullable
     * @return List of Classes for which constructors of this classes are existing, but are not itself
     */
    private static List<Class<?>> getConstructorParameterTypes(Class<?> clazz, Class<?> prefer) {

        Constructor<?>[] constructors = clazz.getConstructors();
        List<Class<?>> res = new ArrayList<>();
        for (Constructor<?> c : constructors) {
            Class<?>[] ptypes = c.getParameterTypes();
            if (ptypes.length == 1 && !clazz.equals(ptypes[0])) {
                res.add(ptypes[0]);
            }
            if (prefer != null && ptypes.length == 1 && ptypes[0].equals(prefer)) {
                return Arrays.asList(prefer);
            }
        }
        return res;
    }
}
