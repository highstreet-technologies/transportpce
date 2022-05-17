/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer;

import java.util.HashMap;
import java.util.Map;

public class ObjectSerializerMap extends HashMap<String, ObjectSerializer>{

    private static final long serialVersionUID = 1L;

    // constants
    // end of constants

    // variables
    private final Map<String,ObjectSerializer> others;
    // end of variables

    // constructors
    public ObjectSerializerMap() {
        this.others = new HashMap<>();
    }

    // end of constructors

    // getters and setters
    // end of getters and setters

    // private methods
    private String getKey(String parentClassName, String propertyName) {
        return String.format("%s:%s", parentClassName,propertyName);
    }
    private String getKey(Class<?> parentClass, String propertyName) {
        return this.getKey(parentClass.getName(),propertyName);
    }
    // end of private methods

    // public methods
    @Override
    public ObjectSerializer getOrDefault(Object key, ObjectSerializer defaultValue) {
        return this.getOrDefault(key, null, null, defaultValue);
    }
    public ObjectSerializer getOrDefault(Object key, Class<?> parentClass, String propertyName, ObjectSerializer defaultValue) {
        if(parentClass!=null && propertyName!=null) {
            return this.others.getOrDefault(getKey(parentClass,propertyName), defaultValue);
        }
        return super.getOrDefault(key, defaultValue);
    }

    public void put(Class<?> parentClass, String propertyName, ObjectSerializer value) {
        this.others.put(getKey(parentClass, propertyName), value);
    }
    public void put(String parentClazzName, String propertyName, ObjectSerializer value) {
        this.others.put(getKey(parentClazzName, propertyName), value);
    }
    // end of public methods


    // static methods
    // end of static methods

    // private classes
    // end of private classes
}
