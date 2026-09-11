/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.client;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.opendaylight.yangtools.binding.data.codec.api.BindingDataCodec;
import org.opendaylight.yangtools.binding.data.codec.impl.BindingCodecContext;
import org.opendaylight.yangtools.binding.generator.impl.DefaultBindingRuntimeGenerator;
import org.opendaylight.yangtools.binding.meta.YangModelBindingProvider;
import org.opendaylight.yangtools.binding.meta.YangModuleInfo;
import org.opendaylight.yangtools.binding.runtime.api.AbstractBindingRuntimeContext;
import org.opendaylight.yangtools.binding.runtime.api.BindingRuntimeGenerator;
import org.opendaylight.yangtools.binding.runtime.api.DefaultBindingRuntimeContext;
import org.opendaylight.yangtools.binding.runtime.spi.ModuleInfoSnapshotResolver;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.parser.impl.DefaultYangParserFactory;
import org.opendaylight.yangtools.yang.xpath.api.YangXPathParserFactory;
import org.opendaylight.yangtools.yang.xpath.impl.AntlrXPathParserFactory;

/**
 * Factory for creating a {@link BindingDataCodec} that is scoped to the OpenROADM device model (7.1.0) and its
 * augmentations.
 *
 * <p>The classpath may contain multiple revisions of some OpenROADM modules (e.g. {@code org-openroadm-interfaces}
 * rev191129 from the 7.1.0 model set and rev220930 from the 13.1.1 model set). A {@link BindingDataCodec} built from
 * the full classpath would resolve identityref values to the latest revision, which does not match the revision
 * expected by the generated binding classes (e.g. {@code Interface.getType()} returns {@code InterfaceType} from
 * rev191129). This factory avoids that by registering only the device model, its transitive imports, and modules that
 * augment the device model.
 */
public final class SbRestconfDataCodecFactory {

    private SbRestconfDataCodecFactory() {
        // utility class
    }

    /**
     * Create a {@link BindingDataCodec} backed by the {@code org-openroadm-device} 7.1.0 model and its augmentations.
     *
     * @param dataRootClass the generated {@code DataRoot} interface for the device model (e.g.
     *                      {@code OrgOpenroadmDeviceData})
     * @return a scoped {@link BindingDataCodec}
     */
    public static BindingDataCodec createForDeviceModel(Class<?> dataRootClass) {
        final YangXPathParserFactory xpathFactory = new AntlrXPathParserFactory();
        DefaultYangParserFactory yangParserFactory = new DefaultYangParserFactory(xpathFactory);
        var snapshotResolver = new ModuleInfoSnapshotResolver("sb-restconf-client", yangParserFactory);

        // Start with the device module and its transitive imports
        YangModuleInfo deviceModuleInfo = findModuleInfo(dataRootClass);
        Set<YangModuleInfo> moduleInfos = collectTransitiveImports(deviceModuleInfo);

        // Also include augmentation modules that import the device module (e.g.
        // org-openroadm-optical-transport-interfaces which augments the interface list with the "ots" container).
        // These modules use the same revision of org-openroadm-interfaces as the device model, so they are safe.
        QName deviceQname = deviceModuleInfo.getName();
        ServiceLoader<YangModelBindingProvider> yangProviderLoader = ServiceLoader.load(YangModelBindingProvider.class);
        for (YangModelBindingProvider yangModelBindingProvider : yangProviderLoader) {
            YangModuleInfo info = yangModelBindingProvider.getModuleInfo();
            if (moduleInfos.contains(info)) {
                continue;
            }
            for (YangModuleInfo imp : info.getImportedModules()) {
                if (imp.getName().equals(deviceQname)) {
                    moduleInfos.addAll(collectTransitiveImports(info));
                    break;
                }
            }
        }

        snapshotResolver.registerModuleInfos(moduleInfos);
        var moduleInfoSnapshot = snapshotResolver.takeSnapshot();
        final BindingRuntimeGenerator bindingRuntimeGenerator = new DefaultBindingRuntimeGenerator();
        final var bindingRuntimeTypes = bindingRuntimeGenerator
                .generateTypeMapping(moduleInfoSnapshot.modelContext());
        AbstractBindingRuntimeContext runtimeContext =
                new DefaultBindingRuntimeContext(bindingRuntimeTypes, moduleInfoSnapshot);
        return new BindingCodecContext(runtimeContext);
    }

    /**
     * Find the {@link YangModuleInfo} for the given {@code DataRoot} class by scanning the
     * {@link YangModelBindingProvider} service loader entries and matching the module package.
     *
     * @param dataRootClass the generated {@code DataRoot} interface (e.g. {@code OrgOpenroadmDeviceData})
     * @return the matching {@link YangModuleInfo}
     * @throws IllegalStateException if no matching module info is found
     */
    private static YangModuleInfo findModuleInfo(Class<?> dataRootClass) {
        String className = dataRootClass.getName();
        ServiceLoader<YangModelBindingProvider> loader = ServiceLoader.load(YangModelBindingProvider.class);
        for (YangModelBindingProvider provider : loader) {
            YangModuleInfo info = provider.getModuleInfo();
            String providerClass = provider.getClass().getName();
            // The provider class is in a "svc" subpackage, e.g.
            // org.opendaylight.yang.svc.v1.http.org.openroadm.device.rev200529.YangModelBindingProviderImpl
            // The DataRoot is in:
            // org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev200529.OrgOpenroadmDeviceData
            // Both share the same suffix after "v1."
            String providerSuffix = providerClass.substring(providerClass.indexOf(".v1.") + 4,
                    providerClass.lastIndexOf("."));
            String dataRootSuffix = className.substring(className.indexOf(".v1.") + 4,
                    className.lastIndexOf("."));
            if (providerSuffix.equals(dataRootSuffix)) {
                return info;
            }
        }
        throw new IllegalStateException("No YangModuleInfo found for " + className);
    }

    /**
     * Collect the given {@link YangModuleInfo} and all its transitive imports via
     * {@link YangModuleInfo#getImportedModules()}.
     *
     * @param root the root module info
     * @return a set containing the root and all transitively imported module infos
     */
    private static Set<YangModuleInfo> collectTransitiveImports(YangModuleInfo root) {
        Set<YangModuleInfo> result = new HashSet<>();
        Deque<YangModuleInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            YangModuleInfo current = queue.poll();
            if (result.add(current)) {
                for (YangModuleInfo imported : current.getImportedModules()) {
                    queue.add(imported);
                }
            }
        }
        return result;
    }
}
