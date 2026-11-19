/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.transportpce.test.AbstractTest;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev250905.Network;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.Networks;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests the REST endpoints of {@link OpticalApiResource} end to end against a preloaded in-memory MD-SAL datastore.
 *
 * <p>The datastore is preloaded from RFC7951 JSON files in
 * {@code src/test/resources/preload} (the same wire format RESTCONF accepts), then the endpoint methods are invoked
 * directly and their JSON responses are validated against the schemas of {@code optical-api-openapi.yaml}. A
 * wire-format drift between implementation and the published API contract fails the build.
 */
public class OpticalApiResourceTest extends AbstractTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    // uuid of the Frankfurt XPDR link inside the germany-17 topology preload
    private static final String TEST_UUID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

    private static JsonSchema nodeSchema;
    private static JsonSchema portMappingSchema;
    private static JsonSchema linkSchema;
    private static JsonSchema serviceCreateRequestSchema;
    private static JsonSchema serviceDeleteRequestSchema;
    private static OpticalApiResource resource;

    @BeforeAll
    static void preloadDatastoreAndLoadSchemas() throws Exception {
        // --- preload the datastore from RFC7951 JSON files ---
        DataBroker dataBroker = getDataBroker();
        var codecServices = getDataStoreContextUtil().getBindingDOMCodecServices();

        JsonStringConverter<Network> portmappingConverter = new JsonStringConverter<>(codecServices);
        Network portmappingNetwork = portmappingConverter.createDataObjectFromInputStream(
                YangInstanceIdentifier.of(Network.QNAME),
                resourceStream("/preload/portmapping-network.json"),
                JSONCodecFactorySupplier.RFC7951);

        JsonStringConverter<Networks> networksConverter = new JsonStringConverter<>(codecServices);
        Networks ietfNetworks = networksConverter.createDataObjectFromInputStream(
                YangInstanceIdentifier.of(Networks.QNAME),
                resourceStream("/preload/ietf-networks.json"),
                JSONCodecFactorySupplier.RFC7951);

        WriteTransaction writeTx = dataBroker.newWriteOnlyTransaction();
        writeTx.merge(LogicalDatastoreType.CONFIGURATION,
                DataObjectIdentifier.builder(Network.class).build(), portmappingNetwork);
        writeTx.merge(LogicalDatastoreType.CONFIGURATION,
                DataObjectIdentifier.builder(Networks.class).build(), ietfNetworks);
        writeTx.commit().get();

        resource = new OpticalApiResource(dataBroker, codecServices);

        // --- extract schemas from the OpenAPI spec ---
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        try (InputStream spec = OpticalApiResourceTest.class.getResourceAsStream("/optical-api-openapi.yaml")) {
            java.util.Map<String, Object> specMap = new Yaml().load(spec);
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> components =
                    (java.util.Map<String, Object>) specMap.get("components");
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> schemas =
                    (java.util.Map<String, Object>) components.get("schemas");
            nodeSchema = buildSchema(factory, schemas, "PortMappingNode");
            portMappingSchema = buildSchema(factory, schemas, "PortMapping");
            linkSchema = buildSchema(factory, schemas, "NetworkLink");
            serviceCreateRequestSchema = buildSchema(factory, schemas, "ServiceCreateRequest");
            serviceDeleteRequestSchema = buildSchema(factory, schemas, "ServiceDeleteRequest");
        }
    }

    private static InputStream resourceStream(String name) throws IOException {
        InputStream stream = OpticalApiResourceTest.class.getResourceAsStream(name);
        if (stream == null) {
            throw new IOException("Preload resource not found: " + name);
        }
        return stream;
    }

    private static JsonSchema buildSchema(JsonSchemaFactory factory,
            java.util.Map<String, Object> schemas, String name) {
        String schemaJson = MAPPER.valueToTree(java.util.Map.of(
                "$ref", "#/components/schemas/" + name,
                "components", java.util.Map.of("schemas", schemas))).toString();
        return factory.getSchema(schemaJson);
    }

    @Test
    void getNodesReturnsPreloadedNodesMatchingSchema() throws IOException {
        JsonNode array = MAPPER.readTree(resource.getNodes());
        assertTrue(array.isArray(), "GET /nodes must return a JSON array");
        assertEquals(69, array.size(), "both preloaded nodes must be returned");
        for (JsonNode entry : array) {
            var nodeId = entry.get("node-id").asText();
            if(nodeId.startsWith("XPDR")) {
                assertEquals("XPDR-", nodeId.substring(0, 5));
            }
            else {
                assertEquals("ROADM-", nodeId.substring(0, 6));
            }
            Set<ValidationMessage> violations = nodeSchema.validate(entry);
            assertTrue(violations.isEmpty(),
                    "node JSON must conform to PortMappingNode schema: " + violations);
        }
    }

    @Test
    void getNodeReturnsSingleNodeMatchingSchema() throws IOException {
        JsonNode node = MAPPER.readTree(resource.getNode("XPDR-UlmMuen"));
        assertEquals("XPDR-UlmMuen", node.get("node-id").asText());
        assertEquals("NodeUlmMuen", node.get("node-info").get("node-clli").asText());
        assertTrue(node.has("mapping"), "node response must include port mappings");
        Set<ValidationMessage> violations = nodeSchema.validate(node);
        assertTrue(violations.isEmpty(),
                "node JSON must conform to PortMappingNode schema: " + violations);
    }

    @Test
    void getNodeThrowsNotFoundForUnknownNode() {
        assertThrows(javax.ws.rs.NotFoundException.class, () -> resource.getNode("does-not-exist"));
    }

    @Test
    void getPortsReturnsMappingsMatchingSchema() throws IOException {
        JsonNode array = MAPPER.readTree(resource.getPorts("XPDR-UlmMuen"));
        assertTrue(array.isArray(), "GET /nodes/{id}/ports must return a JSON array");
        assertEquals(4, array.size(), "the preloaded node has one port mapping");

        var entry = array.valueStream().filter(e -> "XPDR1-CLIENT1".equals(e.get("logical-connection-point").asText()))
                .findFirst().orElseThrow();
        Set<ValidationMessage> violations = portMappingSchema.validate(entry);
        assertTrue(violations.isEmpty(),
                "port JSON must conform to PortMapping schema: " + violations);
    }

    @Test
    void getLinksByUuidsReturnsPreloadedLinkMatchingSchema() throws IOException {
        JsonNode array = MAPPER.readTree(resource.getLinksByUuids(TEST_UUID));
        assertTrue(array.isArray(), "GET /links/uuids=... must return a JSON array");
        assertEquals(1, array.size(), "the preloaded link carries the requested uuid");
        JsonNode entry = array.get(0);
        assertEquals("ROADM-Frankfurt-SRG3-SRG3-CP-TXRXtoROADM-Frankfurt-DEG2-DEG2-CTP-TXRX",
                entry.get("link-id").asText());
        assertEquals(TEST_UUID, entry.get("device-discovery:uuid").asText());
        Set<ValidationMessage> violations = linkSchema.validate(entry);
        assertTrue(violations.isEmpty(),
                "link JSON must conform to NetworkLink schema: " + violations);
    }

    @Test
    void getLinksByUuidsSkipsUnknownUuids() throws IOException {
        JsonNode empty = MAPPER.readTree(resource.getLinksByUuids("00000000-0000-0000-0000-000000000000"));
        assertTrue(empty.isArray() && empty.isEmpty(), "unknown uuid must yield an empty array");
    }

    @Test
    void schemaValidationDetectsViolations() throws IOException {
        // link-id is required by the NetworkLink schema — dropping it must
        // produce validation errors (guards against an always-green test)
        JsonNode withoutLinkId = MAPPER.readTree(resource.getLinksByUuids(TEST_UUID)).get(0);
        ((com.fasterxml.jackson.databind.node.ObjectNode) withoutLinkId).remove("link-id");
        Set<ValidationMessage> violations = linkSchema.validate(withoutLinkId);
        assertFalse(violations.isEmpty(),
                "validator must reject a link without the required link-id");
    }

    @Test
    void dataproviderPaginationSerializesUintsAsPlainNumbers() throws IOException {
        // The dataprovider POST envelope carries Uint64/Uint32 pagination
        // fields. With the UintJacksonModule registered, they must appear as
        // plain JSON numbers, not as {"value": 5} wrapper objects.
        ObjectMapper mapper = new ObjectMapper().registerModule(new UintJacksonModule());
        org.opendaylight.transportpce.optical.model.DataproviderOutputPagination pagination =
                new org.opendaylight.transportpce.optical.model.DataproviderOutputPagination(
                        org.opendaylight.yangtools.yang.common.Uint64.valueOf(2),
                        org.opendaylight.yangtools.yang.common.Uint32.valueOf(20),
                        org.opendaylight.yangtools.yang.common.Uint64.valueOf(41));

        JsonNode json = mapper.valueToTree(pagination);
        assertEquals("2", json.get("page").asText(), "page must serialize as string number");
        assertEquals(20, json.get("size").asLong(), "size must serialize as plain number");
        assertEquals("41", json.get("total").asText(), "total must serialize as string number");

        // and back: plain JSON numbers must deserialize into Uint wrappers
        org.opendaylight.transportpce.optical.model.DataproviderOutputPagination roundTrip =
                mapper.readValue("{\"page\":3,\"size\":10,\"total\":7}",
                        org.opendaylight.transportpce.optical.model.DataproviderOutputPagination.class);
        assertEquals(3L, roundTrip.getPage().longValue());
        assertEquals(10, roundTrip.getSize().toJava());
        assertEquals(7L, roundTrip.getTotal().longValue());
    }

    @Test
    void dataproviderPaginationDeserializesInputEnvelope() throws IOException {
        // the request envelope (data-provider:input with pagination) must
        // deserialize into the DataproviderInputWrapper including Uint fields
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind
                .ObjectMapper().registerModule(new UintJacksonModule());
        org.opendaylight.transportpce.optical.model.DataproviderInputWrapper wrapper = mapper.readValue("""
            {"data-provider:input":{"pagination":{"page":1,"size":25}}}
            """, org.opendaylight.transportpce.optical.model.DataproviderInputWrapper.class);

        var pagination = wrapper.getInput().getPagination();
        assertEquals(1L, pagination.getPage().longValue(), "page must deserialize from plain number");
        assertEquals(25, pagination.getSize().toJava(), "size must deserialize from plain number");
    }

    @Test
    void readOpticalLinksReturnsRfc7951EntriesInDataproviderEnvelope() throws IOException {
        // dataprovider POST envelope: pagination as plain numbers, link
        // entries inside data in RFC7951 format (same as GET endpoints)
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind
                .ObjectMapper().registerModule(new UintJacksonModule());
        org.opendaylight.transportpce.optical.model.DataproviderInputWrapper input = mapper.readValue("""
            {"data-provider:input":{"pagination":{"page":1,"size":20},
              "filter":[{"property":"device-discovery:uuid","filtervalue":"%s"}]}}
            """.formatted(TEST_UUID), org.opendaylight.transportpce.optical.model.DataproviderInputWrapper.class);

        org.opendaylight.transportpce.optical.model.DataproviderOutputWrapper response =
                resource.getLinks(input);

        JsonNode envelope = mapper.valueToTree(response);
        assertEquals(1L, envelope.get("data-provider:output").get("pagination").get("page")
                .asLong(), "pagination page must be a plain number");
        assertEquals(1L, envelope.get("data-provider:output").get("pagination").get("total")
                .asLong(), "the uuid filter must select exactly one link");

        JsonNode data = envelope.get("data-provider:output").get("data");
        assertTrue(data.isArray() && data.size() == 1, "the filtered link must be in data");

        JsonNode link = data.get(0);
        assertEquals(TEST_UUID, link.get("device-discovery:uuid").asText(),
                "uuid Augmentation must be present");
        assertTrue(link.has("link-id"), "link entries must be RFC7951 (link-id present)");
        assertTrue(link.get("link-id").isTextual(),
                "link-id must be a plain RFC7951 string, not a {value:...} wrapper");

        // and the RFC7951 entry must conform to the NetworkLink schema
        Set<ValidationMessage> violations = linkSchema.validate(link);
        assertTrue(violations.isEmpty(),
                "dataprovider link entry must conform to NetworkLink schema: " + violations);
    }

    @Test
    void serviceCreateExampleRequestMatchesSchema() throws IOException {
        // the documented service-create example payload (from the UI) must
        // validate against the ServiceCreateRequest schema of the spec
        JsonNode request = MAPPER.readTree(
                resourceStream("/preload/service-create-request.json"));
        assertTrue(request.has("input"), "request must carry the input wrapper");
        assertEquals("service-create",
                request.get("input").get("sdnc-request-header").get("rpc-action").asText());
        Set<ValidationMessage> violations = serviceCreateRequestSchema.validate(request);
        assertTrue(violations.isEmpty(),
                "service-create example must conform to ServiceCreateRequest schema: " + violations);
    }

    @Test
    void serviceDeleteExampleRequestMatchesSchema() throws IOException {
        // the documented service-delete example payload must validate
        // against the ServiceDeleteRequest schema of the spec
        JsonNode request = MAPPER.readTree(
                resourceStream("/preload/service-delete-request.json"));
        assertTrue(request.has("input"), "request must carry the input wrapper");
        assertEquals("service-delete",
                request.get("input").get("sdnc-request-header").get("rpc-action").asText());
        Set<ValidationMessage> violations = serviceDeleteRequestSchema.validate(request);
        assertTrue(violations.isEmpty(),
                "service-delete example must conform to ServiceDeleteRequest schema: " + violations);
    }
}
