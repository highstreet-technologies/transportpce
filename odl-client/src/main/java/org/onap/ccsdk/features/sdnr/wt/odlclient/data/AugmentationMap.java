/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opendaylight.yang.gen.v1.http.org.openroadm.resource.rev181019.resource.resource.resource.Interface;
import org.opendaylight.yangtools.yang.binding.Augmentation;

public class AugmentationMap {

    private static final Object LOCK = new Object();
    private static volatile AugmentationMap instance;
    private final Map<Class<?>, List<Class<?>>> map;

    public AugmentationMap() {

        this.map = new HashMap<>();
        map.put(org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface.class,
            Arrays.asList(
                org.opendaylight.yang.gen.v1.http.org.openroadm.otn.otu.interfaces.rev181019.Interface1.class,
                org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1.class,
                org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019.Interface1.class,
                org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1.class));
        map.put(Interface.class, Arrays.asList());
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226
                    .networks.network.Node.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.network.rev200529.Node1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Node1.class,
                        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                            .Node1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Network1.class,
                Arrays.asList());
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                    .networks.network.Link.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Link1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529.Link1.class,
                        org.opendaylight.yang.gen.v1.http.org.openroadm.network.topology.rev200529.Link1.class));
        map.put(org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                    .networks.network.node.TerminationPoint.class,
                Arrays.asList(
                    org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.TerminationPoint1.class,
                    org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529
                            .TerminationPoint1.class));
        map.put(org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019
                    .org.openroadm.device.container.org.openroadm.device.Protocols.class,
                Arrays.asList(org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.Protocols1.class));
    }

//    public <T extends DataObject> List<Class<? extends Augmentation<T>>> getAugmentations(Class<T> clazz){
//        if(org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface.class
//                .isAssignableFrom(clazz)) {
//            return  Arrays.asList(
//              org.opendaylight.yang.gen.v1.http.org.openroadm.otn.otu.interfaces.rev181019.Interface1.class,
//              org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1.class,
//              org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019.Interface1.class,
//              org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1.class);
//        }
//        return null;
//    }

    public List<Object> getAugmentations(
            org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface o1) {

        org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.InterfaceBuilder builder =
            new org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.InterfaceBuilder(o1);
        List<Object> augs = new ArrayList<>();
        Object ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.otn.otu.interfaces.rev181019.Interface1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(
            org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019.Interface1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1.class);
        if (ao != null) {
            augs.add(ao);
        }
        return augs;

    }

//    public List<Object> getAugmentations(Interface o) {
//        InterfaceBuilder builder = new InterfaceBuilder(o);
//        List<Object> augs = new ArrayList<>();
//        //        Object ao = builder.augmentation(org.opendaylight.yang.gen.v1
//        //                .http.org.openroadm.optical.transport.interfaces.rev181019.Interface1.class);
//        //        if(ao!=null) {
//        //            augs.add(ao);
//        //        }
//        return augs;
//    }

    public List<Object> getAugmentations(
            org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226
                .networks.network.Node o1) {

        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226
                .networks.network.NodeBuilder builder =
            new org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226
                .networks.network.NodeBuilder(o1);
        List<Object> augs = new ArrayList<>();
        Object ao = builder.augmentation(org.opendaylight.yang.gen.v1.http.org.openroadm.network.rev200529.Node1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Node1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Node1.class);
        if (ao != null) {
            augs.add(ao);
        }
        return augs;
    }


    public List<Object> getAugmentations(
            org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.Link o1) {

        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.LinkBuilder builder =
            new org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.LinkBuilder(o1);
        List<Object> augs = new ArrayList<>();
        Object ao = builder
                .augmentation(org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.Link1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529.Link1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder
                .augmentation(org.opendaylight.yang.gen.v1.http.org.openroadm.network.topology.rev200529.Link1.class);
        if (ao != null) {
            augs.add(ao);
        }
        return augs;
    }

    public List<Object> getAugmentations(
            org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.node.TerminationPoint o1) {

        org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.node.TerminationPointBuilder builder =
            new org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226
                .networks.network.node.TerminationPointBuilder(o1);
        List<Object> augs = new ArrayList<>();
        Object ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev200529.TerminationPoint1.class);
        if (ao != null) {
            augs.add(ao);
        }
        ao = builder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.otn.network.topology.rev200529.TerminationPoint1.class);
        if (ao != null) {
            augs.add(ao);
        }
        return augs;
    }

    public List<Object> getAugmentations(
            org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019
                .org.openroadm.device.container.org.openroadm.device.Protocols o1) {

        org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019
                .org.openroadm.device.container.org.openroadm.device.ProtocolsBuilder builder =
            new org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019
                .org.openroadm.device.container.org.openroadm.device.ProtocolsBuilder(o1);
        List<Object> augs = new ArrayList<>();

        Object ao =
                builder.augmentation(org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.Protocols1.class);
        if (ao != null) {
            augs.add(ao);
        }
        return augs;
    }

    public Collection<Object> getAugmentations(Object object) {
        if (!(object instanceof Augmentation)) {
            return null;
        }
        if (object
                instanceof org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface) {
            return this.getAugmentations(
                    (org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface) object);
        } else if (object instanceof Interface) {
            return this.getAugmentations((Interface)object);
        }
        return null;
    }

    public static AugmentationMap getInstance() {
        //TODO check if this synchronized lock cannot be repaled by a straightorward initilization elsewhere
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AugmentationMap();
                }
            }
        }
        return instance;
    }

}
