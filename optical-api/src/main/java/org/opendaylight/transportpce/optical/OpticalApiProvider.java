/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lighty.server.LightyJettyServerProvider;
import javax.servlet.ServletException;
import javax.ws.rs.ext.ContextResolver;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.opendaylight.aaa.web.ServletDetails;
import org.opendaylight.aaa.web.WebContext;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.yangtools.concepts.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers the Optical API (JAX-RS) on the same Jetty server that serves RESTCONF.
 *
 * <p>Exposes optical network data (port mapping, node information) as plain JSON
 * for UI applications (e.g. odlux-nx networkViewApp). Intentionally bypasses the YANG-RPC contract: this is a
 * fork-specific, lightweight northbound interface.
 *
 * <p>Endpoints (base path {@code /optical-api}):
 * <ul>
 *   <li>{@code GET /optical-api/nodes} — all port-mapping nodes with node info</li>
 *   <li>{@code GET /optical-api/nodes/{nodeId}} — single node with its port mappings</li>
 *   <li>{@code GET /optical-api/nodes/{nodeId}/ports} — port mappings of a node</li>
 * </ul>
 */
public class OpticalApiProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OpticalApiProvider.class);
    private static final String CONTEXT_PATH = "/optical-api";

    private final LightyJettyServerProvider serverProvider;
    private final DataBroker dataBroker;
    private final org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices codecServices;
    private Registration webContextRegistration;

    public OpticalApiProvider(LightyJettyServerProvider serverProvider, DataBroker dataBroker,
            org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices codecServices) {
        this.serverProvider = serverProvider;
        this.dataBroker = dataBroker;
        this.codecServices = codecServices;
        LOG.info("OpticalApiProvider created");
    }

    /**
     * Register the JAX-RS application on the Jetty server.
     */
    public void start() throws ServletException {
        // ObjectMapper with yangtools Uint wrapper support, exposed to the
        // Jersey Jackson provider via a ContextResolver (the standard JAX-RS
        // way to customize the mapper). Without it, Uint64/Uint32 pagination
        // fields serialize as {"value": 5} instead of 5.
        ObjectMapper objectMapper =
                new ObjectMapper()
                        .registerModule(new UintJacksonModule());
        ContextResolver<ObjectMapper> mapperResolver = type -> objectMapper;

        // register the resource instance (not the class) so that the DataBroker
        // constructor argument does not need to be resolvable by HK2
        ResourceConfig config = new ResourceConfig()
                .register(new OpticalApiResource(dataBroker, codecServices))
                .register(JacksonFeature.class)
                .register(mapperResolver);
        ServletContainer servlet = new ServletContainer(config);

        WebContext webContext = WebContext.builder()
                .name("OpticalApi")
                .contextPath(CONTEXT_PATH)
                .supportsSessions(false)
                .addServlet(ServletDetails.builder()
                        .servlet(servlet)
                        .addUrlPattern("/*")
                        .build())
                .build();

        webContextRegistration = serverProvider.getServer().registerWebContext(webContext);
        LOG.info("Optical API registered at {} on RESTCONF server", CONTEXT_PATH);
    }

    @Override
    public void close() {
        if (webContextRegistration != null) {
            webContextRegistration.close();
            LOG.info("Optical API unregistered");
        }
    }
}
