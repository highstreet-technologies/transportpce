/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer.ObjectSerializer;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer.ObjectSerializerMap;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer.SerializerElem;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.network.Node;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.node.TerminationPoint;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yangtools.yang.binding.BaseIdentity;
import org.opendaylight.yangtools.yang.binding.ChoiceIn;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Enumeration;
import org.opendaylight.yangtools.yang.binding.TypeObject;
import org.opendaylight.yangtools.yang.common.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OdlDataSerializer {

    abstract SerializerElem preValueWrite(String key, Object o, boolean withNsPrefix, Class<?> rootClass);
    abstract SerializerElem preValueWrite(String key, Object o, boolean withNsPrefix,boolean withNamespace, Class<?> rootClass);

    abstract void postValueWrite(SerializerElem e, String key);

    abstract void onValueWrite(SerializerElem e, Object o);

    abstract void clear();

    private static final Logger LOG = LoggerFactory.getLogger(OdlDataSerializer.class);

    private boolean nullValueExcluded;

    private final ObjectSerializerMap extraMappers;
    private final ClassFinder clsFinder;
    private final Map<Class<?>, List<Class<?>>> augmentations = OdlObjectMapperXml.initAutoAugmentationList();
    private static final ObjectSerializer defaultSerializer = new ObjectSerializer();

    public OdlDataSerializer(ClassFinder clsFinder) {
        this.clsFinder = clsFinder;
        this.extraMappers = new ObjectSerializerMap();
    }

    public void addSerializer(Class<?> clazz, ObjectSerializer s) {
        this.extraMappers.put(clazz.getName(), s);
    }

    public void addSerializer(Class<?> parentClazz, String propertyName, ObjectSerializer s) {
        this.extraMappers.put(parentClazz, propertyName, s);
    }

    public void addSerializer(String parentClazzName, String propertyName, ObjectSerializer s) {
        this.extraMappers.put(parentClazzName, propertyName, s);
    }

    public void setNullValueExcluded(boolean exclude) {
        this.nullValueExcluded = exclude;
    }

    public boolean isExcludeNullValue() {
        return this.nullValueExcluded;
    }

    public <T extends DataObject> String writeValueAsString(T value, String rootKey) {

        this.clear();
        Class<?> rootClass = value.getClass();
        SerializerElem e = this.startElem(rootKey, value, false, rootClass);
        this.writeRecurseProperties(e, value, 0, rootClass);
        this.stopElem(e, rootKey);
        return e.toString();
    }

    @SuppressWarnings("unchecked")
    private void writeRecurseProperties(SerializerElem e, Object object, int level, Class<?> rootClass) {
        ObjectSerializer extraSerializer = null;
        if (level > 15) {
            System.out.println("Level to deep protection.");
        } else {
            if (object != null) {
                Class<?> clazz = object.getClass();
                Field[] fields = clazz.getDeclaredFields();
                boolean found = false;
                Field nameField = null;
                SerializerElem e2 = null;
                for (Field field : fields) {
                    try {
                        String name = field.getName();
                        field.setAccessible(true);
                        Object value = field.get(object);
                        //only _xxx properties are interesting
                        if (name.startsWith("_")) {
                            if (this.nullValueExcluded && value == null) {
                                continue;
                            }
                            Class<?> type = field.getType();
                            extraSerializer =
                                    this.extraMappers.getOrDefault(type.getName(), clazz, name, defaultSerializer);
                            //convert property name to kebab-case (yang-spec writing)
                            name = extraSerializer.convertPropertyName(name);
                            //if has inner childs
                            if (DataObject.class.isAssignableFrom(type)) {
                                e2 = this.startElem(name, value, false, rootClass);
                                this.writeRecurseProperties(e2, value, level + 1, rootClass);
                                this.stopElem(e2, name);
                                e.addChild(e2);
                            } else {
                                //if enum
                                if (Enum.class.isAssignableFrom(type)) {
                                    e2 = this.startElem(name, value, false, false, rootClass);
                                    String svalue = this.getEnumStringValue(value);
                                    this.writeElemValue(e2, svalue.substring(0, 1).toLowerCase() + svalue.substring(1));
                                    this.stopElem(e2, name);
                                    e.addChild(e2);
                                }
                                // type object (new type of base type) => use getValue()
                                else if (TypeObject.class.isAssignableFrom(type)) {
                                    e2 = this.startElem(name, value, false, false, rootClass);
                                    this.writeElemValue(e2, this.getTypeObjectStringValue(value, type));
                                    this.stopElem(e2, name);
                                    e.addChild(e2);
                                }
                                //if choice then jump over field and step into next java level, but not in xml
                                else if (ChoiceIn.class.isAssignableFrom(type)) {
                                    this.writeRecurseProperties(e, value, level, rootClass);
                                }
                                //if type is Identity reference
                                else if (type == Class.class && BaseIdentity.class.isAssignableFrom((Class<?>) value)) {
                                    e2 = this.startElem(name, value, rootClass);
                                    this.writeElemValue(e2, this.getIdentity((Class<?>) value));
                                    this.stopElem(e2, name);
                                    e.addChild(e2);
                                }
                                //if list of elems
                                else if (value != null && Map.class.isAssignableFrom(type)) {
                                    for (Object listObject : ((Map<?,Object>) value).values()) {
                                        e2 = this.startElem(name, listObject, rootClass);
                                        this.writeRecurseProperties(e2, listObject, level + 1, rootClass);
                                        this.stopElem(e2, name);
                                        e.addChild(e2);
                                    }
                                }
                                //if list of elems
                                else if (value != null && List.class.isAssignableFrom(type)) {
                                    for (Object listObject : (List<Object>) value) {
                                        e2 = this.startElem(name, listObject, rootClass);
                                        this.writeRecurseProperties(e2, listObject, level + 1, rootClass);
                                        this.stopElem(e2, name);
                                        e.addChild(e2);
                                    }
                                }
                                //by exclude all others it is basic value element
                                else {
                                    e2 = this.startElem(name, value, false, false, rootClass);
                                    this.writeElemValue(e2, value);
                                    this.stopElem(e2, name);
                                    e.addChild(e2);
                                }
                                found = true;

                            }
                        } else if (name.equals("name")) {
                            nameField = field;
                        }
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        LOG.warn("problem accessing value during mapping: ", ex);
                    }
                }
                if (!found && nameField != null) {
                    try {
                        Object value = nameField.get(object);
                        Class<?> type = nameField.getType();
                        if (type == String.class) {
                            this.writeElemValue(e, this.getIdentity((String) value));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException ex) {
                        LOG.warn("problem accessing value during mapping2: ", ex);
                    }
                }
                Collection<Object> augmentations = getAugmentations(object, clazz);
                if (augmentations != null && augmentations.size() > 0) {
                    for (Object augment : augmentations) {
                        this.writeRecurseProperties(e, augment, level, augment.getClass());
                    }
                }
            }
        }
    }



    private Collection<Object> getAugmentations(Object object, Class<?> clazz) {
        if (object instanceof Augmentable) {
            if (object instanceof Interface) {
                return AugmentationMap.getInstance().getAugmentations((Interface) object);
            }
            if (object instanceof Node) {
                return AugmentationMap.getInstance().getAugmentations((Node) object);
            }
            if (object instanceof Link) {
                return AugmentationMap.getInstance().getAugmentations((Link) object);
            }
            if (object instanceof TerminationPoint) {
                return AugmentationMap.getInstance().getAugmentations((TerminationPoint) object);
            }
        }
        return null;
    }

    private SerializerElem startElem(String elem, Object o, Class<?> rootClass) {
        return this.startElem(elem, o, true, rootClass);
    }

    private SerializerElem startElem(String elem, Object o, boolean withNsPrefix, Class<?> rootClass) {
        return this.preValueWrite(elem, o, withNsPrefix, rootClass);
    }
    private SerializerElem startElem(String elem, Object o, boolean withNsPrefix, boolean withNamespace, Class<?> rootClass) {
        return this.preValueWrite(elem, o, withNsPrefix, withNamespace, rootClass);
    }

    private void writeElemValue(SerializerElem e, Object elemValue) {
        this.onValueWrite(e, elemValue);
    }

    private void stopElem(SerializerElem e, String elem) {
        this.postValueWrite(e, elem);
    }

    private String getIdentity(String clsName)
            throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        Class<?> cls = this.clsFinder == null ? null : this.clsFinder.findClass(clsName);
        return (cls != null && BaseIdentity.class.isAssignableFrom(cls)) ? this.getIdentity(cls) : null;
    }

    private String getIdentity(Class<?> value) throws IllegalArgumentException, IllegalAccessException {
        QName qname = null;
        Field[] fields = value.getDeclaredFields();
        for (Field f : fields) {
            if (f.getName() == "QNAME") {
                qname = (QName) f.get(value);
                break;
            }
        }
        return qname == null ? null : qname.getLocalName();
    }

    private Object getTypeObjectStringValue(Object value, Class<?> type) {
        if (value != null) {
            try {
                Method method = type.getMethod("getValue");
                return method.invoke(value);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                LOG.warn("problem calling getValue fn: ", e);
            }
        }
        return null;
    }

    private String getEnumStringValue(Object value) {
        if (Enumeration.class.isAssignableFrom(value.getClass())) {
            return ((Enumeration) value).getName();
        }

        return String.valueOf(value);
    }
}
