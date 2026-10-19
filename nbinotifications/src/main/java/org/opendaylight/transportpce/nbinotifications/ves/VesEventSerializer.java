/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.ves;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.common.serialization.Serializer;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader.Domain;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader.Priority;
import org.opendaylight.transportpce.devicediscovery.model.ves.Event;
import org.opendaylight.transportpce.devicediscovery.model.ves.NotificationFields;
import org.opendaylight.transportpce.devicediscovery.ves.VesEventWrapper;
import org.opendaylight.transportpce.nbinotifications.serialization.ConfigConstants;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactory;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonWriterFactory;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka serializer producing ONAP VES events (domain 'notification') from
 * MD-SAL data objects.
 *
 * <p>The wrapped data object is serialized to RFC7951 JSON with an inference
 * path built up to the object's parent, so nested data objects such as list
 * entries (e.g. ietf-network-topology links) are serialized correctly. The
 * object's fields are exposed in the VES notificationFields.additionalFields.
 * changeIdentifier carries the fully qualified name of the binding class,
 * changeType carries created / updated / deleted as observed by a
 * DataTreeChangeListener.
 */
public class VesEventSerializer implements Serializer<VesEvent<? extends DataObject>> {

    private static final Logger LOG = LoggerFactory.getLogger(VesEventSerializer.class);

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private BindingDOMCodecServices codecServices;
    private String reportingEntityId = "";
    private String reportingEntityName = "transportpce";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        LOG.info("VesEventSerializer configuration {}", configs);
        if (configs.containsKey(ConfigConstants.CONVERTER)
                && configs.get(ConfigConstants.CONVERTER) instanceof BindingDOMCodecServices services) {
            codecServices = services;
        }
        if (configs.containsKey(ConfigConstants.REPORTING_ENTITY_ID)
                && configs.get(ConfigConstants.REPORTING_ENTITY_ID) instanceof String entityId) {
            reportingEntityId = entityId;
        }
        if (configs.containsKey(ConfigConstants.REPORTING_ENTITY_NAME)
                && configs.get(ConfigConstants.REPORTING_ENTITY_NAME) instanceof String entityName) {
            reportingEntityName = entityName;
        }
    }

    @Override
    public byte[] serialize(String topic, VesEvent<? extends DataObject> data) {
        if (codecServices == null) {
            throw new IllegalArgumentException("Codec services should be configured through configure method");
        }
        if (data == null || data.getDataObject() == null) {
            return new byte[0];
        }
        try {
            JsonNode additionalFieldsNode = toAdditionalFieldsNode(
                    serializeDataObject((DataObjectIdentifier<DataObject>) data.getIdentifier(),
                        (DataObject) data.getDataObject()));
            LOG.debug("Serialized data object for VES additionalFields: {}", additionalFieldsNode);
            Event event = new Event();
            event.setCommonEventHeader(createHeader(data));
            event.setNotificationFields(createNotificationFields(data));

            // additionalFields must carry the data object's original JSON
            // tree, including nested objects (e.g. source / destination of a
            // link). The generated VES HashMap POJO only supports
            // Map<String, String>, therefore it is injected as a raw JSON
            // node after converting the event POJO to a tree.
            ObjectNode eventNode = mapper.valueToTree(new VesEventWrapper(event));
            ObjectNode notificationFields = (ObjectNode) eventNode.get("event").get("notificationFields");
            notificationFields.set("additionalFields", additionalFieldsNode);
            byte[] payload = mapper.writeValueAsBytes(eventNode);
            LOG.info("Serialized VES event: {}", new String(payload, StandardCharsets.UTF_8));
            return payload;
        } catch (IOException e) {
            LOG.error("exception raised during VES event serialization", e);
            return new byte[0];
        }
    }

    private CommonEventHeader createHeader(VesEvent<? extends DataObject> data) {
        double epochMicrosec = System.currentTimeMillis() * 1000.0;
        int sequence = (int) SEQUENCE.incrementAndGet();
        CommonEventHeader header = new CommonEventHeader();
        header.setDomain(Domain.NOTIFICATION);
        header.setEventId(reportingEntityName + "_" + data.getChangeType().getValue() + "_" + sequence);
        header.setEventName(data.getDataObject().implementedInterface().getName() + "_"
                + data.getChangeType().getValue());
        header.setLastEpochMicrosec(epochMicrosec);
        header.setPriority(Priority.NORMAL);
        header.setReportingEntityId(reportingEntityId);
        header.setReportingEntityName(reportingEntityName);
        header.setSequence(sequence);
        header.setSourceName(data.getSourceName());
        header.setStartEpochMicrosec(epochMicrosec);
        return header;
    }

    private NotificationFields createNotificationFields(VesEvent<? extends DataObject> data) throws IOException {
        NotificationFields fields = new NotificationFields();
        fields.setChangeIdentifier(data.getDataObject().implementedInterface().getName());
        fields.setChangeType(data.getChangeType().getValue());
        return fields;
    }

    /**
     * Serialize a data object to RFC7951 JSON rooted at the object itself.
     *
     * <p>The binding object is converted to its normalized node via the
     * codec's toNormalizedDataObject. The JSON writer is created with an
     * inference reaching the object's parent statement, so also list entries
     * like links are written with correct schema context. This is not
     * possible via JsonStringConverter, which always assumes a top-level
     * container.
     *
     * <p>List entries are not valid JSON documents on their own, therefore
     * the entry's enclosing map node is written instead and unwrapped again
     */
    @SuppressWarnings("unchecked")
    private <T extends DataObject> String serializeDataObject(DataObjectIdentifier<T> identifier,
            T dataObject) throws IOException {
        if (codecServices == null) {
            throw new IllegalArgumentException("Codec services should be configured through configure method");
        }
        var result = codecServices.toNormalizedDataObject(
                (org.opendaylight.yangtools.binding.DataObjectReference<DataObject>) identifier,
                (DataObject) dataObject);
        NormalizedNode node = result.node();
        YangInstanceIdentifier path = result.path();

        // For list entries write the enclosing map node instead: a bare map
        // entry is not a valid JSON document root.
        NormalizedNode writeNode;
        int excludeLastSteps;
        if (node instanceof org.opendaylight.yangtools.yang.data.api.schema.MapEntryNode entryNode) {
            writeNode = org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNodes
                    .mapNodeBuilder(entryNode.name().getNodeType()).withChild(entryNode).build();
            excludeLastSteps = 2;
        } else {
            writeNode = node;
            excludeLastSteps = 1;
        }

        EffectiveModelContext modelContext = codecServices.getRuntimeContext().modelContext();
        JSONCodecFactory codecFactory = JSONCodecFactorySupplier.RFC7951.getShared(modelContext);

        // Build inference up to (excluding) the written node's own statement.
        // The normalized path contains both the map node and the map entry
        // step for list entries (same QName), which collapse into one schema
        // tree step.
        SchemaInferenceStack inferenceStack = SchemaInferenceStack.of(modelContext);
        var pathArguments = path.getPathArguments();
        org.opendaylight.yangtools.yang.common.QName previous = null;
        for (int i = 0; i < pathArguments.size() - excludeLastSteps; i++) {
            var nodeType = pathArguments.get(i).getNodeType();
            if (nodeType.equals(previous)) {
                continue;
            }
            LOG.debug("Entering schema tree step {}: {}", i, pathArguments.get(i));
            inferenceStack.enterSchemaTree(nodeType);
            previous = nodeType;
        }

        try (Writer writer = new StringWriter();
                com.google.gson.stream.JsonWriter jsonWriter = JsonWriterFactory.createJsonWriter(writer, 4)) {
            NormalizedNodeStreamWriter streamWriter = JSONNormalizedNodeStreamWriter
                    .createExclusiveWriter(codecFactory, inferenceStack.toInference(),
                            writeNode.name().getNodeType().getNamespace(), jsonWriter);
            try (NormalizedNodeWriter nodeWriter = NormalizedNodeWriter.forStreamWriter(streamWriter)) {
                nodeWriter.write(writeNode);
                nodeWriter.flush();
            }
            return writer.toString();
        }
    }

    /**
     * Convert the serialized data object JSON into the additionalFields
     * node, preserving the original tree structure (nested objects stay
     * objects, lists stay arrays).
     */
    private JsonNode toAdditionalFieldsNode(String json) throws IOException {
        JsonNode node = mapper.readTree(json);
        // Unwrap single-element name/namespace wrappers, e.g.
        // {"ietf-network-topology:link":[{...}]} produced by writing the
        // enclosing map node of a list entry.
        while (node.isObject() && node.size() == 1) {
            JsonNode child = node.iterator().next();
            if (child.isObject()) {
                node = child;
            } else if (child.isArray() && child.size() == 1 && child.get(0).isObject()) {
                node = child.get(0);
            } else {
                break;
            }
        }
        return node;
    }
}
