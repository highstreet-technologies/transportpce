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
import org.opendaylight.transportpce.sbrestconf.config.SbRestconfConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Southbound RESTCONF client for device-level read and write operations using MDSAL type-safe DataObject and
 * DataObjectIdentifier.
 * <p>
 * Serialization and deserialization between DataObject and JSON is handled internally via BindingDataCodec, so callers
 * work exclusively with generated YANG binding classes.
 * <p>
 * URL pattern: {controller-base-url}{mount-prefix}{node-id}/yang-ext:mount{object-path}
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
     * @param path   the DataObjectIdentifier pointing to the object on the mounted device
     * @param clazz  the expected return type
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
     * @param path   the DataObjectIdentifier pointing to the object
     * @param data   the DataObject to write
     * @return true if successful
     */
    public <T extends DataObject> boolean put(String nodeId, DataObjectIdentifier<T> path, T data) {
        return write(nodeId, path, data, "PUT");
    }

    /**
     * Partially update a device-level object via RESTCONF PATCH.
     *
     * @param nodeId the device node-id
     * @param path   the DataObjectIdentifier pointing to the object
     * @param data   the DataObject to patch
     * @return true if successful
     */
    public <T extends DataObject> boolean patch(String nodeId, DataObjectIdentifier<T> path, T data) {
        return write(nodeId, path, data, "PATCH");
    }

    /**
     * Delete a device-level object via RESTCONF DELETE.
     *
     * @param nodeId the device node-id
     * @param path   the DataObjectIdentifier pointing to the object
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
    protected <T extends DataObject> T deserialize(DataObjectIdentifier<T> path, String json) {
        YangInstanceIdentifier yiid = serializer.toYangInstanceIdentifier(path);

        NormalizationResultHolder result = new NormalizationResultHolder();
        try (StringReader reader = new StringReader(json);
                NormalizedNodeStreamWriter streamWriter = ImmutableNormalizedNodeStreamWriter.from(result)) {

            JSONCodecFactory codecFactory = JSONCodecFactorySupplier.RFC7951
                    .getShared(dataCodec.modelContext());

            // RESTCONF JSON wraps the response in a module-qualified key, e.g.:
            //   {"org-openroadm-device:info": { ... }}
            //   {"org-openroadm-device:degree": [ { ... } ]}
            // The parser must start at the parent context of the wrapper key so it
            // can read the module-qualified key and enter the target container or
            // list node. The resulting NormalizedNode tree is therefore rooted at
            // the wrapper-key level, i.e. at pathArgs.get(parentDepth).
            //
            // For a keyed list entry (e.g. Degree[DegreeKey{degreeNumber=1}]), the
            // YangInstanceIdentifier has three path arguments:
            //   0: container (org-openroadm-device)
            //   1: list node (degree)
            //   2: list entry with predicates (degree, degree-number=1)
            // The parser starts at the container level (depth 0), reads the
            // "org-openroadm-device:degree" wrapper key and produces a MapNode
            // (the list) as the parsed root. We then navigate into the MapNode to
            // find the specific entry matching the key predicates.
            var pathArgs = yiid.getPathArguments();
            // The binding codec may emit a keyed list entry as two consecutive path
            // arguments: a bare NodeIdentifier for the list node followed by a
            // NodeIdentifierWithPredicates carrying the key(s), both with the same
            // QName. The RESTCONF JSON wrapper key corresponds to the list node
            // (the bare NodeIdentifier), so the parser must start at the parent of
            // that node. parentDepth is the index of the wrapper-key node, i.e. the
            // last NodeIdentifier (non-predicate) argument in the path.
            int parentDepth = pathArgs.size() - 1;
            if (parentDepth >= 1
                    && pathArgs.get(parentDepth) instanceof YangInstanceIdentifier.NodeIdentifierWithPredicates) {
                parentDepth--;
            }
            SchemaInferenceStack stack = SchemaInferenceStack.of(dataCodec.modelContext());
            // Enter the data tree for each ancestor of the wrapper-key node. Skip
            // NodeIdentifierWithPredicates arguments: they share the QName of the
            // preceding list NodeIdentifier, which has already entered the list
            // node, so entering the same QName again would fail schema resolution.
            for (int i = 0; i < parentDepth; i++) {
                YangInstanceIdentifier.PathArgument arg = pathArgs.get(i);
                if (arg instanceof YangInstanceIdentifier.NodeIdentifierWithPredicates) {
                    continue;
                }
                stack.enterDataTree(arg.getNodeType());
            }
            EffectiveStatementInference inference = stack.toInference();
            JsonParserStream jsonParser = JsonParserStream.create(streamWriter, codecFactory, inference);

            jsonParser.parse(new com.google.gson.stream.JsonReader(reader));

            // The parsed NormalizedNode tree is rooted at the wrapper-key level,
            // i.e. at pathArgs.get(parentDepth). Navigate down to the target node
            // (the last path argument) before calling fromNormalizedNode.
            NormalizedNode data = result.getResult().data();
            for (int i = parentDepth + 1; i < pathArgs.size() && data != null; i++) {
                YangInstanceIdentifier.PathArgument arg = pathArgs.get(i);
                if (data instanceof org.opendaylight.yangtools.yang.data.api.schema.DistinctContainer dc) {
                    NormalizedNode child = dc.childByArg(arg);
                    if (child == null && arg instanceof YangInstanceIdentifier.NodeIdentifierWithPredicates) {
                        // A RESTCONF GET on a specific list entry returns that single
                        // entry wrapped in the list key. The key carried in the
                        // DataObjectIdentifier is used to build the request URL and may
                        // not match the actual key values in the response payload, so
                        // fall back to the (single) entry contained in the parsed list.
                        java.util.Iterator<NormalizedNode> it = dc.body().iterator();
                        if (it.hasNext()) {
                            child = it.next();
                        }
                    }
                    data = child;
                } else {
                    data = null;
                }
            }

            if (data == null) {
                LOG.warn("Target node not found in parsed JSON for path {}", path);
                return null;
            }

            Map.Entry<org.opendaylight.yangtools.binding.DataObjectReference<?>, DataObject> entry =
                    serializer.fromNormalizedNode(yiid, data);
            return (T) entry.getValue();
        } catch (Exception e) {
            LOG.error("Failed to deserialize JSON to {}", path, e);
            return null;
        }
    }

    /**
     * Convert a DataObjectIdentifier to a RESTCONF URL path component.
     *
     * <p>The binding codec may emit a list entry as two consecutive path arguments: a bare
     * {@link YangInstanceIdentifier.NodeIdentifier} for the list node followed by a
     * {@link YangInstanceIdentifier.NodeIdentifierWithPredicates} carrying the key(s). In
     * RESTCONF (RFC 8040) the list entry is expressed as a single path segment
     * {@code list-name=key-value}, so the bare list node is skipped here to avoid duplicating
     * the list name (e.g. {@code /circuit-packs/circuit-packs=cpkey}).
     */
    protected String toRestconfPath(DataObjectIdentifier<?> path) {
        YangInstanceIdentifier yiid = serializer.toYangInstanceIdentifier(path);
        org.opendaylight.yangtools.yang.model.api.EffectiveModelContext modelContext = dataCodec.modelContext();

        StringBuilder sb = new StringBuilder();
        String[] curModule = {null};
        var args = yiid.getPathArguments();
        for (int i = 0; i < args.size(); i++) {
            YangInstanceIdentifier.PathArgument arg = args.get(i);
            // Skip a bare list NodeIdentifier when it is immediately followed by a
            // NodeIdentifierWithPredicates for the same node type; the key selector below
            // already carries the list name.
            if (arg instanceof YangInstanceIdentifier.NodeIdentifier
                    && i + 1 < args.size()
                    && args.get(i + 1) instanceof YangInstanceIdentifier.NodeIdentifierWithPredicates nextNwp
                    && nextNwp.getNodeType().equals(arg.getNodeType())) {
                continue;
            }
            appendNode(sb, modelContext, arg.getNodeType(), curModule);
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
     * Append a single path node (with module prefix when the module changes) and update
     * {@code curModule} to the module of the appended node.
     */
    private void appendNode(StringBuilder sb,
            org.opendaylight.yangtools.yang.model.api.EffectiveModelContext modelContext,
            org.opendaylight.yangtools.yang.common.QName nodeType, String[] curModule) {
        var module = modelContext.findModule(nodeType.getModule()).orElseThrow();
        String moduleName = module.getName();
        if (!moduleName.equals(curModule[0])) {
            curModule[0] = moduleName;
            sb.append("/").append(moduleName).append(":").append(nodeType.getLocalName());
        } else {
            sb.append("/").append(nodeType.getLocalName());
        }
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
