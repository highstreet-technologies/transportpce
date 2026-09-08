/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.data.codec.api.BindingDataCodec;
import org.opendaylight.yangtools.binding.data.codec.api.BindingNormalizedNodeSerializer;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactory;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonParserStream;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonWriterFactory;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizationResultHolder;
import org.opendaylight.yangtools.yang.model.api.EffectiveStatementInference;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;
import org.opendaylight.transportpce.sbrestconf.config.SbRestconfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Southbound RESTCONF client for device-level read and write operations
 * using MDSAL type-safe DataObject and DataObjectIdentifier.
 *
 * Serialization and deserialization between DataObject and JSON is handled
 * internally via BindingDataCodec, so callers work exclusively with
 * generated YANG binding classes.
 *
 * URL pattern:
 *   {controller-base-url}{mount-prefix}{node-id}/yang-ext:mount{object-path}
 */
public class SbRestconfClient implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SbRestconfClient.class);
    private static final String MOUNT_SUFFIX = "/yang-ext:mount";

    private final SbRestconfConfig config;
    private final ControllerUuidResolver uuidResolver;
    private final BindingDataCodec dataCodec;
    private final BindingNormalizedNodeSerializer serializer;
    private final Client client;

    public SbRestconfClient(SbRestconfConfig config, ControllerUuidResolver uuidResolver,
            BindingDataCodec dataCodec) {
        this.config = config;
        this.uuidResolver = uuidResolver;
        this.dataCodec = dataCodec;
        this.serializer = dataCodec.nodeSerializer();
        this.client = ClientBuilder.newClient();
    }

    /**
     * Read a device-level object via RESTCONF GET.
     *
     * @param nodeId the device node-id
     * @param path the DataObjectIdentifier pointing to the object on the mounted device
     * @param clazz the expected return type
     * @return the deserialized DataObject, or empty if not found
     */
    public <T extends DataObject> Optional<T> get(String nodeId, DataObjectIdentifier<T> path, Class<T> clazz) {
        String url = buildUrl(nodeId, toRestconfPath(path));
        if (url == null) {
            return Optional.empty();
        }

        LOG.debug("GET {} ({})", url, clazz.getSimpleName());

        Invocation.Builder request = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getBearerToken())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        try (Response response = request.get()) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String body = response.readEntity(String.class);
                T result = deserialize(path, body);
                LOG.debug("GET {} succeeded", url);
                return Optional.ofNullable(result);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOG.debug("GET {} returned 404", url);
                return Optional.empty();
            } else {
                LOG.error("GET {} returned HTTP {}", url, response.getStatus());
                return Optional.empty();
            }
        } catch (Exception e) {
            LOG.error("GET {} failed", url, e);
            return Optional.empty();
        }
    }

    /**
     * Write or replace a device-level object via RESTCONF PUT.
     *
     * @param nodeId the device node-id
     * @param path the DataObjectIdentifier pointing to the object
     * @param data the DataObject to write
     * @return true if successful
     */
    public <T extends DataObject> boolean put(String nodeId, DataObjectIdentifier<T> path, T data) {
        return write(nodeId, path, data, "PUT");
    }

    /**
     * Partially update a device-level object via RESTCONF PATCH.
     *
     * @param nodeId the device node-id
     * @param path the DataObjectIdentifier pointing to the object
     * @param data the DataObject to patch
     * @return true if successful
     */
    public <T extends DataObject> boolean patch(String nodeId, DataObjectIdentifier<T> path, T data) {
        return write(nodeId, path, data, "PATCH");
    }

    /**
     * Delete a device-level object via RESTCONF DELETE.
     *
     * @param nodeId the device node-id
     * @param path the DataObjectIdentifier pointing to the object
     * @return true if successful
     */
    public boolean delete(String nodeId, DataObjectIdentifier<?> path) {
        String url = buildUrl(nodeId, toRestconfPath(path));
        if (url == null) {
            return false;
        }

        LOG.debug("DELETE {}", url);

        Invocation.Builder request = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getBearerToken());

        try (Response response = request.delete()) {
            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()
                    || response.getStatus() == Response.Status.OK.getStatusCode()) {
                LOG.debug("DELETE {} succeeded", url);
                return true;
            } else {
                LOG.error("DELETE {} returned HTTP {}", url, response.getStatus());
                return false;
            }
        } catch (Exception e) {
            LOG.error("DELETE {} failed", url, e);
            return false;
        }
    }

    private <T extends DataObject> boolean write(String nodeId, DataObjectIdentifier<T> path, T data, String method) {
        String url = buildUrl(nodeId, toRestconfPath(path));
        if (url == null) {
            return false;
        }

        String payload;
        try {
            payload = serialize(path, data);
        } catch (IOException e) {
            LOG.error("Failed to serialize {} for {} {}", data.getClass().getSimpleName(), method, url, e);
            return false;
        }

        LOG.debug("{} {} (payload: {} bytes)", method, url, payload.length());

        Invocation.Builder request = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getBearerToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        try (Response response = request.method(method, Entity.entity(payload, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()
                    || response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()
                    || response.getStatus() == Response.Status.OK.getStatusCode()) {
                LOG.debug("{} {} succeeded with HTTP {}", method, url, response.getStatus());
                return true;
            } else {
                String body = response.readEntity(String.class);
                LOG.error("{} {} returned HTTP {}: {}", method, url, response.getStatus(), body);
                return false;
            }
        } catch (Exception e) {
            LOG.error("{} {} failed", method, url, e);
            return false;
        }
    }

    /**
     * Serialize a DataObject to JSON string using BindingDataCodec.
     */
    private <T extends DataObject> String serialize(DataObjectIdentifier<T> path, T data) throws IOException {
        JSONCodecFactory codecFactory = JSONCodecFactorySupplier.RFC7951
                .getShared(dataCodec.modelContext());

        // Convert DataObject to NormalizedNode
        BindingNormalizedNodeSerializer.NodeResult nodeResult =
                serializer.toNormalizedDataObject(path, data);
        NormalizedNode normalizedNode = nodeResult.node();

        try (Writer writer = new StringWriter();
                var jsonWriter = JsonWriterFactory.createJsonWriter(writer, 0)) {
            EffectiveStatementInference rootNode = SchemaInferenceStack
                    .of(dataCodec.modelContext())
                    .toInference();
            NormalizedNodeStreamWriter jsonStreamWriter = JSONNormalizedNodeStreamWriter
                    .createExclusiveWriter(codecFactory, rootNode,
                            org.opendaylight.yangtools.yang.model.api.EffectiveModelContext.NAME.getNamespace(),
                            jsonWriter);
            try (NormalizedNodeWriter nodeWriter = NormalizedNodeWriter.forStreamWriter(jsonStreamWriter)) {
                nodeWriter.write(normalizedNode);
                nodeWriter.flush();
            }
            return writer.toString();
        }
    }

    /**
     * Deserialize a JSON string to a DataObject using BindingDataCodec.
     */
    @SuppressWarnings("unchecked")
    private <T extends DataObject> T deserialize(DataObjectIdentifier<T> path, String json) {
        YangInstanceIdentifier yiid = serializer.toYangInstanceIdentifier(path);

        NormalizationResultHolder result = new NormalizationResultHolder();
        try (StringReader reader = new StringReader(json);
                NormalizedNodeStreamWriter streamWriter = ImmutableNormalizedNodeStreamWriter.from(result);
                JsonParserStream jsonParser = JsonParserStream.create(streamWriter,
                        JSONCodecFactorySupplier.RFC7951.getShared(dataCodec.modelContext()))) {
            jsonParser.parse(new com.google.gson.stream.JsonReader(reader));
            Map.Entry<org.opendaylight.yangtools.binding.DataObjectReference<?>, DataObject> entry =
                    serializer.fromNormalizedNode(yiid, result.getResult().data());
            return (T) entry.getValue();
        } catch (Exception e) {
            LOG.error("Failed to deserialize JSON to {}", path, e);
            return null;
        }
    }

    /**
     * Convert a DataObjectIdentifier to a RESTCONF URL path component.
     */
    String toRestconfPath(DataObjectIdentifier<?> path) {
        YangInstanceIdentifier yiid = serializer.toYangInstanceIdentifier(path);

        StringBuilder sb = new StringBuilder();
        for (YangInstanceIdentifier.PathArgument arg : yiid.getPathArguments()) {
            sb.append("/").append(arg.getNodeType().getLocalName());
            if (arg instanceof YangInstanceIdentifier.NodeIdentifierWithPredicates nwp) {
                for (var entry : nwp.entrySet()) {
                    sb.append("=").append(entry.getValue().toString());
                }
            }
        }
        String result = sb.toString();
        LOG.debug("Converted DataObjectIdentifier to RESTCONF path: {}", result);
        return result;
    }

    /**
     * Build the full RESTCONF URL for a device-level request.
     */
    String buildUrl(String nodeId, String objectPath) {
        Optional<String> controllerUuidOpt = uuidResolver.resolveControllerUuid(nodeId);
        if (controllerUuidOpt.isEmpty()) {
            LOG.warn("Cannot resolve controller-uuid for node {}", nodeId);
            return null;
        }
        var uuid = controllerUuidOpt.orElseThrow();
        Optional<SbRestconfConfig.ControllerEntry> controllerOpt =
                config.findController(uuid);
        if (controllerOpt.isEmpty()) {
            LOG.warn("Controller UUID {} not found in config", uuid);
            return null;
        }

        String baseUrl = controllerOpt.orElseThrow().getBaseUrl();
        String mountPrefix = config.getMountPrefix();

        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        if (!baseUrl.endsWith("/")) {
            url.append(mountPrefix);
        } else {
            url.append(mountPrefix.substring(1));
        }
        url.append(nodeId);
        url.append(MOUNT_SUFFIX);
        if (objectPath != null && !objectPath.isBlank()) {
            if (!objectPath.startsWith("/")) {
                url.append("/");
            }
            url.append(objectPath);
        }

        return url.toString();
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
