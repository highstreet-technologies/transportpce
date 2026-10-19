/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.ves;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.transportpce.nbinotifications.serialization.ConfigConstants;
import org.opendaylight.transportpce.test.AbstractTest;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.NetworkId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.Networks;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.Network;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.networks.NetworkKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.LinkId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.Network1;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.LinkKey;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;

@Disabled
public class VesEventSerializerTest extends AbstractTest {

    public static final DataObjectIdentifier<Link> LINK_IID = DataObjectIdentifier.builder(Networks.class)
            .child(Network.class,new NetworkKey(new NetworkId("")))
            .augmentation(Network1.class)
            .child(Link.class,new LinkKey(new LinkId("")))
            .build();

    @Test
    void serializeLinkCreatedTest() throws Exception {
        JsonStringConverter<Link> converter =
                new JsonStringConverter<>(getDataStoreContextUtil().getBindingDOMCodecServices());
        String json = Files.readString(Path.of("src/test/resources/ves_link_event.json"));
        YangInstanceIdentifier yid = YangInstanceIdentifier.of(Link.QNAME);
        Link link = converter.createDataObjectFromJsonString(yid, json, JSONCodecFactorySupplier.RFC7951);
        assertNotNull(link, "Link should be deserialized from json");

        VesEvent<Link> event = new VesEvent<>(VesEvent.ChangeType.CREATED,
                LINK_IID, link, "networkmodel");

        VesEventSerializer serializer = new VesEventSerializer();
        Map<String, Object> configs = Map.of(
            ConfigConstants.CONVERTER, getDataStoreContextUtil().getBindingDOMCodecServices(),
            ConfigConstants.REPORTING_ENTITY_ID, "1c60f4fc-bd8b-11f1-8e5e-f7ba5b2a2680",
            ConfigConstants.REPORTING_ENTITY_NAME, "transportpce");
        serializer.configure(configs, false);
        byte[] data = serializer.serialize("test", event);
        serializer.close();

        assertNotNull(data, "Serialized data should not be null");
        JsonNode node = new ObjectMapper().readTree(data);
        JsonNode header = node.get("event").get("commonEventHeader");
        assertNotNull(header, "commonEventHeader should be present");
        assertEquals("notification", header.get("domain").asText());
        assertEquals("transportpce", header.get("reportingEntityName").asText());
        assertEquals("1c60f4fc-bd8b-11f1-8e5e-f7ba5b2a2680", header.get("reportingEntityId").asText());

        JsonNode fields = node.get("event").get("notificationFields");
        assertNotNull(fields, "notificationFields should be present");
        assertEquals("created", fields.get("changeType").asText());
        assertEquals(Link.class.getName(), fields.get("changeIdentifier").asText());
        JsonNode additional = fields.get("additionalFields");
        assertNotNull(additional, "additionalFields should be present");
        assertEquals("ROADM1-DEG1-DEG1-OTH-1-1_to_SRG1-ETHERNET-SRG1-1-2",
                additional.get("link-id").asText());
        assertTrue(additional.has("source"), "additionalFields should contain source");
        assertTrue(additional.has("destination"), "additionalFields should contain destination");
    }

    @Test
    void serializeLinkDeletedTest() throws Exception {
        JsonStringConverter<Link> converter =
                new JsonStringConverter<>(getDataStoreContextUtil().getBindingDOMCodecServices());
        String json = Files.readString(Path.of("src/test/resources/ves_link_event.json"));
        YangInstanceIdentifier yid = YangInstanceIdentifier.of(Link.QNAME);
        Link link = converter.createDataObjectFromJsonString(yid, json, JSONCodecFactorySupplier.RFC7951);

        VesEvent<Link> event = new VesEvent<>(VesEvent.ChangeType.DELETED,
                LINK_IID, link, "networkmodel");

        VesEventSerializer serializer = new VesEventSerializer();
        serializer.configure(Map.of(ConfigConstants.CONVERTER,
                getDataStoreContextUtil().getBindingDOMCodecServices()), false);
        byte[] data = serializer.serialize("test", event);
        serializer.close();

        JsonNode node = new ObjectMapper().readTree(data);
        assertEquals("deleted", node.get("event").get("notificationFields").get("changeType").asText());
    }

    @Test
    void serializeNullDataTest() throws Exception {
        VesEventSerializer serializer = new VesEventSerializer();
        serializer.configure(Map.of(), false);
        byte[] data = serializer.serialize("test", null);
        serializer.close();
        assertNotNull(data);
        assertEquals(0, data.length, "Null data should serialize to empty byte array");
    }
}
