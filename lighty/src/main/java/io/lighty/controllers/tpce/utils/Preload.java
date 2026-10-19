/*
 * Copyright © 2026 Highstreet Technologies and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.lighty.controllers.tpce.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.DataObjectReference;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.data.api.schema.stream.NormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactory;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.opendaylight.yangtools.yang.data.codec.gson.JsonParserStream;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNormalizedNodeStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.schema.NormalizationResultHolder;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.stmt.ContainerEffectiveStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.ModuleEffectiveStatement;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack.Inference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports data from a directory of RFC 7951 JSON files into the MDSAL datastore.
 *
 * <p>Each file must contain a single top-level key that identifies the target module
 * and container, e.g. {@code {"ietf-network:networks": {...}}}. The module is resolved
 * via the effective model context; the parsed NormalizedNode is converted to a binding
 * {@link DataObject} and written with a {@link DataBroker} transaction.
 *
 * <p>Parsing is done on the DOM level with {@link JsonParserStream}, so no compile-time
 * dependency on the generated binding classes of the imported modules is needed.
 */
public class Preload {

    private static final Logger LOG = LoggerFactory.getLogger(Preload.class);

    private final DataBroker dataBroker;
    private final BindingDOMCodecServices codecServices;
    private final EffectiveModelContext modelContext;
    private final JSONCodecFactorySupplier codecSupplier;
    private final Map<String, LogicalDatastoreType> moduleStoreOverrides;

    public Preload(final DataBroker dataBroker, final BindingDOMCodecServices codecServices) {
        this(dataBroker, codecServices, JSONCodecFactorySupplier.RFC7951, Map.of());
    }

    public Preload(final DataBroker dataBroker, final BindingDOMCodecServices codecServices,
            final JSONCodecFactorySupplier codecSupplier,
            final Map<String, LogicalDatastoreType> moduleStoreOverrides) {
        this.dataBroker = dataBroker;
        this.codecServices = codecServices;
        this.modelContext = codecServices.getRuntimeContext().modelContext();
        this.codecSupplier = codecSupplier;
        this.moduleStoreOverrides = Map.copyOf(moduleStoreOverrides);
    }

    /**
     * Reads all {@code *.json} files in the given directory (non-recursive, sorted by
     * file name) and writes their content into the datastore. Files that cannot be
     * resolved or parsed are skipped with a warning; processing continues with the
     * remaining files.
     *
     * @param directory directory containing the JSON files
     * @return the list of successfully imported files
     * @throws IOException if the directory cannot be read
     */
    public List<Path> preloadDirectory(final Path directory) throws IOException {
        final List<Path> imported = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json")) {
            final List<Path> files = new ArrayList<>();
            stream.forEach(files::add);
            files.sort(Comparator.comparing(Path::getFileName, Comparator.naturalOrder()));
            for (final Path file : files) {
                if (preloadFile(file)) {
                    imported.add(file);
                }
            }
            LOG.info("Preload: imported {} of {} JSON file(s) from {}", imported.size(), files.size(), directory);
        }
        return imported;
    }

    /**
     * Imports a single RFC 7951 JSON file. The file must contain exactly one top-level
     * key of the form {@code module:container}.
     *
     * @param file the JSON file
     * @return true if the file was imported, false if it was skipped
     */
    public boolean preloadFile(final Path file) {
        final JsonObject root;
        try {
            root = parseRoot(file);
        } catch (IOException e) {
            LOG.warn("Preload: skipping {} - cannot parse JSON: {}", file, e.getMessage());
            return false;
        }
        final Set<String> keys = root.keySet();
        if (keys.size() != 1) {
            LOG.warn("Preload: skipping {} - expected exactly one top-level key, got {}", file, keys);
            return false;
        }
        final String key = keys.iterator().next();
        return preloadEntry(key, root.getAsJsonObject(key), file);
    }

    /**
     * Imports the JSON object stored under the given top-level key.
     *
     * @param key top-level key of the form {@code module:container}
     * @param data the JSON object mapped by that key
     * @param source source description (file name) for logging only
     * @return true if the entry was imported
     */
    public boolean preloadEntry(final String key, final JsonObject data, final Object source) {
        final int sep = key.indexOf(':');
        if (sep < 0) {
            LOG.warn("Preload: skipping {} - top-level key '{}' has no module prefix", source, key);
            return false;
        }
        final String moduleName = key.substring(0, sep);
        final String containerName = key.substring(sep + 1);

        final ModuleEffectiveStatement module = findModule(moduleName);
        if (module == null) {
            LOG.warn("Preload: skipping {} - module '{}' not found in effective model context", source, moduleName);
            return false;
        }
        final Optional<QName> container = findTopLevelContainer(module, containerName);
        if (container.isEmpty()) {
            LOG.warn("Preload: skipping {} - container '{}' not found in module '{}'",
                    source, containerName, moduleName);
            return false;
        }
        final LogicalDatastoreType store =
                moduleStoreOverrides.getOrDefault(moduleName, defaultStore(moduleName));
        return importContainer(container.orElseThrow(), store, source, key, wrap(key, data));
    }

    /**
     * Rebuilds the original top-level JSON object from key and data. The RFC 7951
     * parser needs the module-prefixed wrapper key to resolve namespaces.
     */
    private JsonObject wrap(final String key, final JsonObject data) {
        final JsonObject wrapped = new JsonObject();
        wrapped.add(key, data);
        return wrapped;
    }

    private boolean importContainer(final QName container, final LogicalDatastoreType store,
            final Object source, final String key, final JsonObject wrapped) {
        final NormalizedNode parsed;
        try {
            parsed = parseNormalizedNode(wrapped);
        } catch (IOException e) {
            LOG.warn("Preload: skipping {} - cannot parse '{}': {}", source, key, e.getMessage());
            return false;
        }
        final Map.Entry<DataObjectReference<?>, DataObject> entry =
                codecServices.fromNormalizedNode(YangInstanceIdentifier.of(container), parsed);
        if (entry == null || entry.getValue() == null || entry.getKey() == null) {
            LOG.warn("Preload: skipping {} - no binding DataObject for '{}'", source, key);
            return false;
        }
        return write(store, entry.getKey().toIdentifier(), entry.getValue(), source, key);
    }

    @SuppressWarnings("unchecked")
    private boolean write(final LogicalDatastoreType store,
            final DataObjectIdentifier<?> identifier, final DataObject value,
            final Object source, final String key) {
        try {
            final WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
            tx.merge(store, (DataObjectIdentifier<DataObject>) identifier, (DataObject) value);
            tx.commit().get(120, TimeUnit.SECONDS);
            LOG.info("Preload: stored '{}' from {} into {}", key, source, store);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Preload: interrupted while storing '{}' from {}", key, source);
        } catch (ExecutionException | TimeoutException e) {
            LOG.warn("Preload: cannot store '{}' from {} into {}: {}",
                    key, source, store, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Default store assignment by YANG module name. Network-model data the PCE and
     * renderer work on (ietf-network / ietf-network-topology topologies, portmapping)
     * lives in CONFIG; everything else defaults to OPERATIONAL.
     */
    private LogicalDatastoreType defaultStore(final String moduleName) {
        switch (moduleName) {
            case "ietf-network":
            case "ietf-network-topology":
            case "transportpce-portmapping":
                return LogicalDatastoreType.CONFIGURATION;
            default:
                return LogicalDatastoreType.OPERATIONAL;
        }
    }

    /**
     * Parses a complete RFC 7951 document (top-level object with its single
     * {@code module:container} wrapper key) into a NormalizedNode rooted at the
     * container, using the same mechanism as
     * {@link org.opendaylight.transportpce.common.converter.JsonStringConverter#createDataObjectFromJsonString}.
     */
    private NormalizedNode parseNormalizedNode(final JsonObject wrapped) throws IOException {
        final NormalizationResultHolder holder = new NormalizationResultHolder();
        final Inference inference = SchemaInferenceStack.of(modelContext).toInference();
        final JSONCodecFactory codecFactory = codecSupplier.getShared(modelContext);
        try (NormalizedNodeStreamWriter streamWriter = ImmutableNormalizedNodeStreamWriter.from(holder);
                JsonParserStream parser = JsonParserStream.create(streamWriter, codecFactory, inference);
                JsonReader reader = new JsonReader(new StringReader(wrapped.toString()))) {
            parser.parse(reader);
            return holder.getResult().data();
        }
    }

    private ModuleEffectiveStatement findModule(final String moduleName) {
        final Collection<ModuleEffectiveStatement> modules = modelContext.findModuleStatements(moduleName);
        return modules.isEmpty() ? null : modules.iterator().next();
    }

    private Optional<QName> findTopLevelContainer(final ModuleEffectiveStatement module,
            final String containerName) {
        return module.streamEffectiveSubstatements(ContainerEffectiveStatement.class)
                .map(ContainerEffectiveStatement::argument)
                .filter(qname -> qname.getLocalName().equals(containerName))
                .findFirst();
    }

    private JsonObject parseRoot(final Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
