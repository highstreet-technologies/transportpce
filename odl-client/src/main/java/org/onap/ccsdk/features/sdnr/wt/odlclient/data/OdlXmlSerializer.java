/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import java.lang.reflect.Field;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer.SerializerElem;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.CircuitPacks;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.RoadmConnections;
import org.opendaylight.yangtools.yang.binding.BaseIdentity;
import org.opendaylight.yangtools.yang.common.QName;

public class OdlXmlSerializer extends OdlDataSerializer {

    private static final String[] xmlNamespacePrefixes = ("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,aa,ab,ac"
            + ",ad,ae,af,ag,ah,ai,aj,ak,al,am,an,ao,ap,aq,ar,as,at,au,av,aw,ax,ay,az").split(",");
    private int prefixIndex = 0;
    private boolean hasNamespace;

    public OdlXmlSerializer() {
        this(null);
    }

    public OdlXmlSerializer(ClassFinder clsFinder) {
        super(clsFinder);
        this.hasNamespace = false;
    }

    private String getNamespaceOrDefault(Class<? extends Object> cls, Class<?> rootClass, String def) {
        final String clsName = cls.getName();
        final String rootClsName = rootClass.getName();

        if (rootClsName.startsWith(CircuitPacks.class.getName())
                || rootClsName.startsWith(RoadmConnections.class.getName())) {
            return def;
        }

        if (clsName.startsWith("org.opendaylight.yang.gen.v1.http.org.openroadm.common.types")) {
            return "http://org/openroadm/common-types";
        }
        if (clsName.startsWith("org.opendaylight.yang.gen.v1.http.org.openroadm.port.types")) {
            return "http://org/openroadm/port/types";
        }

        return def;
    }

    private String getPrefix() {
        return this.prefixIndex < xmlNamespacePrefixes.length ? xmlNamespacePrefixes[this.prefixIndex++] : "";
    }

    @Override
    void clear() {
        this.prefixIndex = 0;
    }

    @Override
    SerializerElem preValueWrite(String key, Object o, boolean withNsPrefix, Class<?> rootClass) {
        final String ns = this.getXmlNameSpace(o, rootClass);
        this.hasNamespace = ns.length() > 0;
        return new SerializerElem(key, ns, (withNsPrefix && hasNamespace) ? this.getPrefix() : null);
    }

    @Override
    SerializerElem preValueWrite(String key, Object o, boolean withNsPrefix, boolean withNamespace,
            Class<?> rootClass) {
        if (withNamespace) {
            return this.preValueWrite(key, o, withNsPrefix, rootClass);
        }
        return new SerializerElem(key, null, null);
    }

    @Override
    void postValueWrite(SerializerElem e, String key) {

    }

    @Override
    void onValueWrite(SerializerElem e, Object o) {
        e.setValue(o);
    }

    private String getXmlNameSpace(Object o, Class<?> rootClass) {
        if (o != null) {
            Field f;
            Object couldQName = null;
            try {

                f = o.getClass().getField("QNAME");
                couldQName = f.get(o);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                //no need to handle this
            }
            if (couldQName instanceof QName) {
                QName qname = (QName) couldQName;
                return qname.getNamespace().toString();
            } else if (o.getClass() == Class.class && BaseIdentity.class.isAssignableFrom((Class<?>) o)) {
                try {
                    Class<?> value = (Class<?>) o;
                    QName qname = null;
                    Field[] fields = value.getDeclaredFields();
                    for (Field f2 : fields) {
                        if (f2.getName() == "QNAME") {
                            qname = (QName) f2.get(value);
                            break;
                        }
                    }
                    if (qname != null) {
                        return qname.getNamespace().toString();
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // no need to catch
                }
            }
            return this.getNamespaceOrDefault(o.getClass(), rootClass, "");
        }
        return "";
    }


}
