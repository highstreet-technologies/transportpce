/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.DataObjectReference;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.MapEntryNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactory;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonWriterFactory;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNodes;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;

/**
 * Serializes MD-SAL binding data objects to RFC7951 JSON — the same wire
 * format as returned by {@code /restconf/data/...} endpoints.
 *
 * <p>The binding object is converted to its normalized node via the codec's
 * toNormalizedDataObject. The JSON writer is created with an inference reaching
 * the object's parent statement, so also list entries are written with correct
 * schema context. This mirrors the serialization used by the VES event
 * serializer in nbinotifications (VesEventSerializer).
 */
public final class Rfc7951Serializer {

    private final BindingDOMCodecServices codecServices;

    public Rfc7951Serializer(BindingDOMCodecServices codecServices) {
        this.codecServices = codecServices;
    }

    /**
     * Serialize a data object to RFC7951 JSON rooted at the object itself.
     *
     * @param <T> binding data object type
     * @param identifier data object identifier (path of the object)
     * @param dataObject the binding data object
     * @return RFC7951 JSON string
     * @throws IOException if serialization fails
     */
    @SuppressWarnings("unchecked")
    public <T extends DataObject> String serializeDataObject(DataObjectIdentifier<T> identifier,
            T dataObject) throws IOException {
        var result = codecServices.toNormalizedDataObject(
                (DataObjectReference<DataObject>) identifier, (DataObject) dataObject);
        NormalizedNode node = result.node();
        YangInstanceIdentifier path = result.path();

        // For list entries write the enclosing map node instead: a bare map
        // entry is not a valid JSON document root.
        NormalizedNode writeNode;
        int excludeLastSteps;
        if (node instanceof MapEntryNode entryNode) {
            writeNode = ImmutableNodes.mapNodeBuilder(entryNode.name().getNodeType()).withChild(entryNode).build();
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
        List<YangInstanceIdentifier.PathArgument> pathArguments = path.getPathArguments();
        QName previous = null;
        for (int i = 0; i < pathArguments.size() - excludeLastSteps; i++) {
            var nodeType = pathArguments.get(i).getNodeType();
            if (nodeType.equals(previous)) {
                continue;
            }
            inferenceStack.enterSchemaTree(nodeType);
            previous = nodeType;
        }

        try (Writer writer = new StringWriter();
                com.google.gson.stream.JsonWriter jsonWriter = JsonWriterFactory.createJsonWriter(writer, 2)) {
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
     * Unwrap single-element name/namespace wrappers, e.g.
     * {@code {"transportpce-portmapping:nodes":[{...}]}} produced by writing
     * the enclosing map node of a list entry, down to the entry object itself.
     *
     * @param json RFC7951 JSON string as produced by {@link #serializeDataObject}
     * @return unwrapped JSON string
     * @throws IOException if parsing fails
     */
    public static String unwrapSingleElement(String json) throws IOException {
        com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(json);
        while (node.isObject() && node.size() == 1) {
            com.fasterxml.jackson.databind.JsonNode child = node.iterator().next();
            if (child.isObject()) {
                node = child;
            } else if (child.isArray() && child.size() == 1 && child.get(0).isObject()) {
                node = child.get(0);
            } else {
                break;
            }
        }
        return node.toString();
    }
}
