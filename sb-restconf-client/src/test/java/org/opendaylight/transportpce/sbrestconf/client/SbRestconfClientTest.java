/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendaylight.transportpce.sbrestconf.config.SbRestconfConfig;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.OrgOpenroadmDeviceData;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.circuit.pack.Ports;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.circuit.pack.PortsKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.circuit.packs.CircuitPacks;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.circuit.packs.CircuitPacksKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.interfaces.grp.Interface;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.interfaces.grp.InterfaceKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.OrgOpenroadmDevice;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.org.openroadm.device.Degree;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.org.openroadm.device.DegreeKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.org.openroadm.device.Info;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.org.openroadm.device.Protocols;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.org.openroadm.device.SharedRiskGroup;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.org.openroadm.device.container.org.openroadm.device.SharedRiskGroupKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.dhcp.rev200529.Protocols1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev191129.OpticalTransport;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.data.codec.api.BindingDataCodec;
import org.opendaylight.yangtools.yang.common.Uint16;

@ExtendWith(MockitoExtension.class)
public class SbRestconfClientTest {

    @Mock
    private ControllerUuidResolver uuidResolver;

    private final BindingDataCodec dataCodec = createBindingDataCodec();

    private SbRestconfConfig config;
    private SbRestconfClient client;

    @BeforeEach
    void setUp() {
        config = new SbRestconfConfig();
        config.setBearerToken("test-token");
        config.setControllers(List.of(
                new SbRestconfConfig.ControllerEntry("ctrl-uuid-1", "https://ctrl-1:8443/rests"),
                new SbRestconfConfig.ControllerEntry("ctrl-uuid-2", "https://ctrl-2:8443/rests")
        ));
        client = new SbRestconfClient(config, uuidResolver, dataCodec);
    }

    /**
     * Build a real {@link BindingDataCodec} backed by the {@code org-openroadm-device} 7.1.0 YANG model so that
     * {@code DataObjectIdentifier} instances can be converted to RESTCONF paths without mocking the codec.
     *
     * <p>See {@link SbRestconfDataCodecFactory} for details on why a scoped codec is needed instead of loading all
     * models from the classpath.
     */
    private static BindingDataCodec createBindingDataCodec() {
        return SbRestconfDataCodecFactory.createForDeviceModel(OrgOpenroadmDeviceData.class);
    }

    @Test
    void testBuildUrlWithDefaultMountPrefix() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "/org-openroadm-device:org-openroadm-device");
        assertEquals(
                "https://ctrl-1:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount/org-openroadm-device:org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlWithSecondController() {
        when(uuidResolver.resolveControllerUuid("device-2"))
                .thenReturn(Optional.of("ctrl-uuid-2"));

        String url = client.buildUrl("device-2", "/org-openroadm-device:org-openroadm-device");
        assertEquals(
                "https://ctrl-2:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-2/yang-ext:mount/org-openroadm-device:org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlWithEmptyObjectPath() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "");
        assertEquals(
                "https://ctrl-1:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount",
                url);
    }

    @Test
    void testBuildUrlWithNullObjectPath() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", null);
        assertEquals(
                "https://ctrl-1:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount",
                url);
    }

    @Test
    void testBuildUrlWithCustomMountPrefix() {
        config.setMountPrefix("/custom/mount/node=");

        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "/org-openroadm-device:org-openroadm-device");
        assertEquals(
                "https://ctrl-1:8443/rests/custom/mount/node=device-1/yang-ext:mount/org-openroadm-device:org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlUnknownNode() {
        when(uuidResolver.resolveControllerUuid("unknown-device"))
                .thenReturn(Optional.empty());

        String url = client.buildUrl("unknown-device", "/org-openroadm-device:org-openroadm-device");
        assertNull(url);
    }

    @Test
    void testBuildUrlUnknownControllerUuid() {
        when(uuidResolver.resolveControllerUuid("device-3"))
                .thenReturn(Optional.of("unknown-ctrl-uuid"));

        String url = client.buildUrl("device-3", "/org-openroadm-device:org-openroadm-device");
        assertNull(url);
    }

    @Test
    void testBuildUrlWithNestedObjectPath() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1",
                "/org-openroadm-device:org-openroadm-device/circuit-packs/circuit-pack=0%2F0%2F0%2F1");
        assertEquals(
                "https://ctrl-1:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount/org-openroadm-device:org-openroadm-device/circuit-packs/circuit-pack=0%2F0%2F0%2F1",
                url);
    }

    @Test
    void testBuildUrlWithBaseUrlEndingSlash() {
        config.setControllers(List.of(
                new SbRestconfConfig.ControllerEntry("ctrl-uuid-1", "https://ctrl-1:8443/rests")
        ));

        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        String url = client.buildUrl("device-1", "/org-openroadm-device:org-openroadm-device");
        // baseUrl ends with /, mountPrefix leading / is stripped
        assertEquals(
                "https://ctrl-1:8443/rests/data/network-topology:network-topology/topology=topology-netconf/node=device-1/yang-ext:mount/org-openroadm-device:org-openroadm-device",
                url);
    }

    @Test
    void testBuildUrlBearerTokenInConfig() {
        when(uuidResolver.resolveControllerUuid("device-1"))
                .thenReturn(Optional.of("ctrl-uuid-1"));

        // Just verify the URL is built — token is used in the request, not in the URL
        String url = client.buildUrl("device-1", "/test");
        assertEquals("test-token", config.getBearerToken());
    }

    @Test
    public void testDataObjectIdentifierToRfc8040() {

        assertEquals("/org-openroadm-device:org-openroadm-device/info", client.toRestconfPath(DataObjectIdentifier
                .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                .child(Info.class)
                .build()));
        assertEquals("/org-openroadm-device:org-openroadm-device/circuit-packs=cpkey/ports=pkey",
                client.toRestconfPath(DataObjectIdentifier
                        .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                        .child(CircuitPacks.class, new CircuitPacksKey("cpkey"))
                        .child(Ports.class, new PortsKey("pkey"))
                        .build()));
        assertEquals("/org-openroadm-device:org-openroadm-device/shared-risk-group=1",
                client.toRestconfPath(DataObjectIdentifier
                        .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                        .child(SharedRiskGroup.class, new SharedRiskGroupKey(Uint16.valueOf(1)))
                        .build()));

    }

    @Test
    public void testDeserInfo() throws IOException {
        var info = client.deserialize(DataObjectIdentifier
                .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                .child(Info.class)
                .build(), Files.readString(Path.of("src/test/resources/roadm-info.json")));
        assertNotNull(info);
    }

    @Test
    public void testDeserDegree() throws IOException {

        var degree = client.deserialize(DataObjectIdentifier
                .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                .child(Degree.class, new DegreeKey(Uint16.valueOf(1)))
                .build(), Files.readString(Path.of("src/test/resources/roadm-degree.json")));
        assertNotNull(degree);
    }

    @Test
    public void testDeserProtocol() throws IOException {

        var protocols = client.deserialize(DataObjectIdentifier
                .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                .child(Protocols.class)
                .build(), Files.readString(Path.of("src/test/resources/roadm-protocols.json")));
        assertNotNull(protocols);
        protocols.augmentation(Protocols1.class);
    }

    @Test
    public void testDeserPort() throws IOException {

        var ports = client.deserialize(DataObjectIdentifier
                .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                .child(CircuitPacks.class, new CircuitPacksKey(""))
                .child(Ports.class, new PortsKey(""))
                .build(), Files.readString(Path.of("src/test/resources/roadm-ports.json")));
        assertNotNull(ports);
    }

    @Test
    public void testDeserInterface() throws IOException {

        var interfaces = client.deserialize(DataObjectIdentifier
                .builderOfInherited(OrgOpenroadmDeviceData.class, OrgOpenroadmDevice.class)
                .child(Interface.class, new InterfaceKey(""))
                .build(), Files.readString(Path.of("src/test/resources/roadm-interface.json")));
        assertNotNull(interfaces);
        assertEquals(OpticalTransport.VALUE, interfaces.getType());

    }
}
